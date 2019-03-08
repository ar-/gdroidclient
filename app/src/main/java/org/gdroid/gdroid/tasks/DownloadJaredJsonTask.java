/*
 * Copyright (C) 2018,2019 Andreas Redmer <ar-gdroid@abga.be>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.gdroid.gdroid.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import org.gdroid.gdroid.AppCollectionAdapter;
import org.gdroid.gdroid.MainActivity;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CategoryBean;
import org.gdroid.gdroid.beans.TagBean;
import org.gdroid.gdroid.pref.Pref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadJaredJsonTask extends AsyncTask<String, Void, List<ApplicationBean>> {

    public static final String TAG = "DownloadJaredJsonTask";
    private static final String GDROID_JAR_URL = "https://gitlab.com/gdroid/gdroiddata/raw/master/metadata/gdroid.jar";

    protected Context mContext;
    protected final MainActivity mMainActivity;
    private final AppCollectionAdapter mAppCollectionAdapter;
    protected final String mJsonFileInJar;
    private String mUrl= null;

    // parameter is the adapter that can be notified after processing
    public DownloadJaredJsonTask(MainActivity mainActivity, AppCollectionAdapter appCollectionAdapter, String jsonFileInJar) {
        mMainActivity = mainActivity;
        if (mMainActivity != null)
            mContext = mMainActivity.getApplicationContext();
        mAppCollectionAdapter = appCollectionAdapter;
        mJsonFileInJar=jsonFileInJar;
    }

    @Override
    protected List<ApplicationBean> doInBackground(String... urls) {
        List<ApplicationBean> abl = new ArrayList<>();
        long lastUpdateCheck = Pref.get().getLastUpdateCheck();
        long now = System.currentTimeMillis();
        mUrl = urls[0];

        // bomb out if there was a successful update in the last 5 minutes
        if (now - lastUpdateCheck < DateUtils.MINUTE_IN_MILLIS * 5 )
        {
            Log.i(TAG, "Not downloading anything, since another download was successful recently");
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(mMainActivity.findViewById(R.id.fab), R.string.data_is_up_to_date, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            return abl;
        }

        try {

            final boolean isRepoDataCurrent = isRepoDataCurrent();
            if (!isRepoDataCurrent)
            {
                // we are outdated: let's update repo and gdroid metadata
                Log.i(TAG, "we are outdated: let's update repo and gdroid metadata");
                abl = loadJsonFromNetwork(mUrl, mJsonFileInJar, new AppBeanJsonParser());
            }
            else
            {
                // repo data has no updates - an update might be in the metadata though
                Log.i(TAG, "repo data has no updates - an update might be in the metadata though");
                final boolean isMetaDataCurrent = isMetaDataCurrent();
                if (isMetaDataCurrent)
                {
                    // everything is up-to-date, we can jump out here
                    Log.i(TAG, "Not downloading anything, since fdroid's and gdroid's etags didn't change");
                    // arriving here without error means a complete update has been successfully done
                    Pref.get().setLastUpdateCheck(System.currentTimeMillis());
                    mMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mMainActivity.findViewById(R.id.fab), R.string.data_is_up_to_date, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    return abl;
                }
                else
                {
                    Log.i(TAG, "metadata has updates");
                    // repo data has no updates - an update is in the metadata though
                    // load all app beans from DB to attach metadata to it
                    AppDatabase db = AppDatabase.get(mContext);
                    abl = db.appDao().getAllApplicationBeansList();
                    db.close();
                }
            }

            // executing the task doesn't work correctly. one async task can't call another async task
            // solution: moved all functionality out of task classes, all doInBackground() directly
            final MetaDownloadJaredJsonTask task = new MetaDownloadJaredJsonTask(mMainActivity, "metadata/gdroid.json", abl);
            task.doInBackground(GDROID_JAR_URL);

            // report back to the UI, that downloading is over and processing has begun
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(mMainActivity.findViewById(R.id.fab), R.string.saving_app_details, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            // update the local DB
            AppDatabase db = AppDatabase.get(mContext);

            // insert all apps at once
            db.appDao().insertApplicationBeans(abl);

            // categories
            final List<CategoryBean> allCategoryMappings = new ArrayList<>();
            for (ApplicationBean ab: abl) {
                final List<CategoryBean> categoryList = ab.getCategoryList();
                if (categoryList !=null)
                {
                    allCategoryMappings.addAll(categoryList);
                    //TODO the delete can also run in one txn, but all it has to collect all to be deleted before updating apps
                    db.appDao().deleteCategoriesForApp(ab.id);
                }
            }
            db.appDao().insertCategories(allCategoryMappings);

            // tags
            final List<TagBean> allTagMappings = new ArrayList<>();
            for (ApplicationBean ab: abl) {
                final List<TagBean> tagList = ab.getTagList();
                if (tagList !=null)
                {
                    if (! tagList.isEmpty())
                    {
                        allTagMappings.addAll(tagList);
                        db.appDao().deleteTagsForApp(ab.id);
                    }
                }
            }
            db.appDao().insertTags(allTagMappings);

            db.close();

            // update the UI after DB has been updated (on the first 2 tabs)
            if (mAppCollectionAdapter != null) {
                for (AppCollectionDescriptor acd : mAppCollectionAdapter.getAppCollectionDescriptorList()) {
                    acd.updateAppsInCollection();
                }
            }

            return abl;
        } catch (IOException e) {
            // error downloading fdroid repo
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(mMainActivity.findViewById(R.id.fab), R.string.download_error, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            return abl;
        }
    }

    @Override
    protected void onPostExecute(List<ApplicationBean> result) {
        SwipeRefreshLayout swipe = mMainActivity.findViewById(R.id.swiperefresh);
        swipe.setRefreshing(false);
        mMainActivity.findViewById(R.id.fab).setEnabled(true);
        if (mAppCollectionAdapter != null) {
            mAppCollectionAdapter.notifyDataSetChanged();
        }
        Log.i(TAG,"download complete");

        // run another async task to update the etags
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String etagFDroid = HttpHeadChecker.getEtag(mUrl);
                String etagGDroid = HttpHeadChecker.getEtag(GDROID_JAR_URL);
                Log.e(TAG,"etag fdroid : "+etagFDroid + " etag gdroid : "+etagGDroid  );
                Pref.get().setLastFDroidEtag(etagFDroid);
                Pref.get().setLastGDroidEtag(etagGDroid);
            }
        });
    }

    protected List<ApplicationBean> loadJsonFromNetwork(String urlString, String jsonFileInJar, JsonParser parser) throws IOException {
        String jsonString = getJsonStringFromFileInJar(urlString, jsonFileInJar);

        return parser.getApplicationBeansFromJson(jsonString);
    }

//    @Nullable

    @NonNull
    private String getJsonStringFromFileInJar(String urlString, String jsonFileInJar) throws IOException {
        String fileName = urlString.substring( urlString.lastIndexOf('/')+1, urlString.length() );
        InputStream stream = null;

        String jsonString ="{}";
        try {
            stream = downloadUrl(urlString);
            OutputStream outputStream =
                    new FileOutputStream(new File(mContext.getCacheDir()+"/"+fileName));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            JarReader jr = new JarReader(mContext.getCacheDir()+"/"+fileName);
            final byte[] bytes = jr.getResource(jsonFileInJar);
            System.gc(); // bugfix #120 : possible OOM kill in next line.
            jsonString = new String(bytes);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return jsonString;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    private boolean isRepoDataCurrent() {
        String etagLocal = Pref.get().getLastFDroidEtag();
        if (TextUtils.isEmpty(etagLocal))
            return false; // makes the first run faster and prevents from comparing the empty string or null
        String etagOnline = HttpHeadChecker.getEtag(mUrl);
//        Pref.get().setLastFDroidEtag(etagOnline); // don't update here, becasue download can still fail (or be aborted0 later
        return etagLocal.equals(etagOnline);
    }

    private boolean isMetaDataCurrent() {
        String etagLocal = Pref.get().getLastGDroidEtag();
        if (TextUtils.isEmpty(etagLocal))
            return false; // makes the first run faster and prevents from comparing the empty string or null
        String etagOnline = HttpHeadChecker.getEtag(GDROID_JAR_URL);
//        Pref.get().setLastGDroidEtag(etagOnline); // don't update here, becasue download can still fail (or be aborted0 later
        return etagLocal.equals(etagOnline);
    }
}
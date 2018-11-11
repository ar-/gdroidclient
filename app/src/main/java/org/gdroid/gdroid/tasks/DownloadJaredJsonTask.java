/*
 * Copyright (C) 2018 Andreas Redmer <ar-gdroid@abga.be>
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
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import org.gdroid.gdroid.AppBeanAdapter;
import org.gdroid.gdroid.AppCollectionAdapter;
import org.gdroid.gdroid.MainActivity;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CategoryBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DownloadJaredJsonTask extends AsyncTask<String, Void, List<ApplicationBean>> {


    private final Context mContext;
    private final MainActivity mMainActivity;
    private final AppCollectionAdapter mAppCollectionAdapter;
    private final String mJsonFileInJar;

    // parameter is the adapter that can be notified after processing
    public DownloadJaredJsonTask(MainActivity mainActivity, AppCollectionAdapter appCollectionAdapter, String jsonFileInJar) {
        mMainActivity = mainActivity;
        mContext = mMainActivity.getApplicationContext();
        mAppCollectionAdapter = appCollectionAdapter;
        mJsonFileInJar=jsonFileInJar;
    }

    @Override
    protected List<ApplicationBean> doInBackground(String... urls) {
        List<ApplicationBean> ret = new ArrayList<>();
            try {
                ret = loadJsonFromNetwork(urls[0], mJsonFileInJar);

                // update the local DB
                AppDatabase db = AppDatabase.get(mContext);

                for (ApplicationBean ab: ret) {
                    db.appDao().insertApplicationBeans(ab);
                    final List<CategoryBean> categoryList = ab.getCategoryList();
                    if (categoryList !=null)
                    {
                        db.appDao().deleteCategoriesForApp(ab.id);
                        for (CategoryBean c: categoryList) {
                            db.appDao().insertCategories(c);
                        }
                    }
                }

                db.close();

                // update the UI after DB has been updated (on the first 2 tabs)
                if (mAppCollectionAdapter != null) {
                    for (AppCollectionDescriptor acd : mAppCollectionAdapter.getAppCollectionDescriptorList()) {
                        acd.updateAppsInCollection();
                    }
                }

                return ret;
            } catch (IOException e) {
                return ret;
//            return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return ret;
//            return getResources().getString(R.string.xml_error);
            }
        }

    @Override
    protected void onPostExecute(List<ApplicationBean> result) {
        mMainActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        mMainActivity.findViewById(R.id.fab).setEnabled(true);
        if (mAppCollectionAdapter != null) {
            mAppCollectionAdapter.notifyDataSetChanged();
        }
        Log.e("DownloadXmlTask","download complete");
        }

    private List<ApplicationBean> loadJsonFromNetwork(String urlString, String jsonFileInJar) throws XmlPullParserException, IOException {
        String jsonString = getJsonStringFromFileInJar(urlString, jsonFileInJar);

        // report back to the UI, that downloading is over and processing has begun
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(mMainActivity.findViewById(R.id.fab), "Processing data ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        JsonParser abjp = new AppBeanJsonParser();
        List<ApplicationBean> entries = abjp.getApplicationBeansFromJson(jsonString);
        return entries;
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
}
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

import android.arch.persistence.room.Ignore;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.gdroid.gdroid.AppCollectionAdapter;
import org.gdroid.gdroid.MainActivity;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CategoryBean;
import org.gdroid.gdroid.xml.FDroidRepoXmlParser;
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

    // parameter is the adapter that can be notified after processing
    public DownloadJaredJsonTask(MainActivity mainActivity, AppCollectionAdapter appCollectionAdapter) {
        mMainActivity = mainActivity;
        mContext = mMainActivity.getApplicationContext();
        mAppCollectionAdapter = appCollectionAdapter;
    }

    @Override
    protected List<ApplicationBean> doInBackground(String... urls) {
        List<ApplicationBean> ret = new ArrayList<>();
            try {
                ret = loadXmlFromNetwork(urls[0]);

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

                // update the UI after DB has been updated
                for (AppCollectionDescriptor acd :mAppCollectionAdapter.getAppCollectionDescriptorList()) {
                    acd.updateAppsInCollection();
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
//            setContentView(R.layout.main);
//            // Displays the HTML string in the UI via a WebView
//            WebView myWebView = (WebView) findViewById(R.id.webview);
//            myWebView.loadData(result, "text/html", null);

//        List<AppCollectionDescriptor> l = mAppCollectionAdapter.getAppCollectionDescriptorList();
//        result.get(0);
//        l.get(0).setName(result.get(0).name);
//        l.get(1).setName(result.get(1).name);
//        l.get(2).setName(result.get(2).name);
//        for (ApplicationBean ab: result) {
//            SimpleApplicationDao.class
//        }
        mMainActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        mMainActivity.findViewById(R.id.fab).setEnabled(true);
        mAppCollectionAdapter.notifyDataSetChanged();
        Log.e("DownloadXmlTask","download complete");
        }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private List<ApplicationBean> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        FDroidRepoXmlParser dfroidRepoXmlParser = new FDroidRepoXmlParser();
        List<ApplicationBean> entries = null;

        String jsonString ="{}";
        try {
            stream = downloadUrl(urlString);
            OutputStream outputStream =
                    new FileOutputStream(new File(mContext.getCacheDir()+"/i.jar"));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            JarResources jr = new JarResources(mContext.getCacheDir()+"/i.jar");
            final byte[] bytes = jr.getResource("index-v1.json");
            jsonString = new String(bytes);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        try {
            JSONObject jo=new JSONObject(jsonString);
            final JSONArray apps = jo.getJSONArray("apps");
            entries = new ArrayList<>(apps.length());
            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.getJSONObject(i);
                try {
                    ApplicationBean ab = jSonObjToAppBean(app);
                    entries.add(ab);
                } catch (JSONException e)
                {
                    Log.e("Parser","ignoring "+app.optString("name", "unknown app"));
                    e.printStackTrace();
                    //skip
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entries;
    }

    private ApplicationBean jSonObjToAppBean(JSONObject app) throws JSONException {
        ApplicationBean ab = new ApplicationBean();
        ab.id = app.getString("packageName");
        ab.added = app.getLong("added");
        ab.lastupdated = app.getLong("lastUpdated");
        ab.license = app.getString("license");
        ab.icon = app.optString("icon");

        ab.name = getLocalizedStringItem(app, "name");
        ab.summary = getLocalizedStringItem(app, "summary"); // summary is optional
        ab.desc = getLocalizedStringItem(app, "description");

        //get categories as string separated list
        ab.categories = new ArrayList<>();
        final JSONArray cats = app.getJSONArray("categories");
        for (int i=0;i<cats.length();i++)
        {
            ab.categories.add(cats.get(i).toString());
        }

        ab.web = app.optString("webSite");
        ab.source = app.optString("sourceCode");
        ab.tracker = app.optString("issueTracker");
        ab.changelog = app.optString("changelog");
        ab.bitcoin = app.optString("bitcoin");
        ab.liberapay = app.optString("liberapayID");
        ab.marketversion = app.optString("suggestedVersionName");
        ab.marketvercode = app.optString("suggestedVersionCode");
        ab.author = app.optString("authorName");
        ab.email = app.optString("authorEmail");

        // TODO apk name from packaes
        ab.apkname = "testXXXXXXXXXXXX.apk";

        // TODO antifeatures lie kcategires
        ab.antifeatures = app.optString("antiFeatures");

        //TODo permissions from packages
        ab.permissions = app.optString("webSite");

        return ab;
    }

    private String getLocalizedStringItem(JSONObject app, String name) throws JSONException {
        final String repoLocale="en";

        // Map locale -> itemcontent
        Map<String, String> availalableLocales = new HashMap<>();

        //gather available Locales for this item
        final String unLocalisedItem = app.optString(name);
        if (!TextUtils.isEmpty(unLocalisedItem))
        {
            availalableLocales.put(repoLocale,unLocalisedItem);
        }

        final JSONObject localized = app.optJSONObject("localized");
        if (localized != null)
        {
            Iterator<String> keys = localized.keys();

            while(keys.hasNext()) {
                String resLocale = keys.next();
                final JSONObject localizedJSONObject = localized.getJSONObject(resLocale);
                final String localisedString = localizedJSONObject.optString(name);
                if (!TextUtils.isEmpty(localisedString))
                {
                    availalableLocales.put(resLocale,localisedString);
                }
            }
        }

        // for some items (like 'name') having none is bad, others (like 'whatsNew') are optional
        if (availalableLocales.isEmpty())
            return null;

        final Set<String> keySet = availalableLocales.keySet();
        String[] resLocales = keySet.toArray(new String[keySet.size()]);
        final String usableLocale = Util.getUsableLocale(resLocales);

        // this can't be null anymore, otherwise we wouldn't have arrived here
        return availalableLocales.get(usableLocale);
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
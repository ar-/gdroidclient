package org.gdroid.gdroid.tasks;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.AppCollectionAdapter;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.xml.FDroidRepoXmlParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DownloadXmlTask extends AsyncTask<String, Void, List<ApplicationBean>> {


    private final Context mContext;
    private final AppCollectionAdapter mAppCollectionAdapter;

    // parameter is the adapter that can be notified after processing
    public DownloadXmlTask(Context applicationContext, AppCollectionAdapter appCollectionAdapter) {
        mContext = applicationContext;
        mAppCollectionAdapter = appCollectionAdapter;
    }

    @Override
    protected List<ApplicationBean> doInBackground(String... urls) {
        List<ApplicationBean> ret = new ArrayList<>();
            try {
                ret = loadXmlFromNetwork(urls[0]);

                AppDatabase db = Room.databaseBuilder(mContext,
                        AppDatabase.class, AppDatabase.db).build();

                for (ApplicationBean ab: ret) {
                    db.appDao().insertApplicationBeans(ab);
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
        String title = null;
        String url = null;
        String summary = null;
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

        // Checks whether the user set the preference to include summary text
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
//        htmlString.append("<h3>" + getResources().getString(R.string.page_title) + "</h3>");
//        htmlString.append("<em>" + getResources().getString(R.string.updated) + " " +
//                formatter.format(rightNow.getTime()) + "</em>");

        try {
            stream = downloadUrl(urlString);
            entries = dfroidRepoXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // FDroidRepoXmlParser returns a List (called "entries") of Entry objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.
//        for (Entry entry : entries) {
//            htmlString.append("<p><a href='");
//            htmlString.append(entry.link);
//            htmlString.append("'>" + entry.title + "</a></p>");
//            // If the user set the preference to include summary text,
//            // adds it to the display.
////            if (pref) {
////                htmlString.append(entry.summary);
////            }
//        }
        return entries;
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
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

package org.gdroid.gdroid;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CategoryBean;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.security.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class AppDetailActivity extends AppCompatActivity {

    Context mContext;
    ApplicationBean mApp;
    FloatingActionButton fab;
    FloatingActionButton fabShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String appId = getIntent().getStringExtra("appId");

        AppDatabase db = AppDatabase.get(getApplicationContext());
        mApp = db.appDao().getApplicationBean(appId);

        // just a data-repair on the display (too easy to do in repo)
        repairMissingData();

        toolbar.setTitle(mApp.name);
        toolbarLayout.setTitle(mApp.name);
        ((TextView)findViewById(R.id.lbl_app_name)).setText(mApp.name);
        ((TextView)findViewById(R.id.lbl_app_summary)).setText(mApp.summary);
        Date lastUpdateDate = new Date(mApp.lastupdated );
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ((TextView)findViewById(R.id.lbl_lastupdated)).setText(sdf.format(lastUpdateDate));
        ((TextView)findViewById(R.id.lbl_app_author)).setText(mApp.author);
        ((TextView)findViewById(R.id.lbl_license)).setText(mApp.license);
        ((TextView)findViewById(R.id.lbl_website)).setText(mApp.web);
        ((TextView)findViewById(R.id.lbl_email)).setText(mApp.email);

        if (!TextUtils.isEmpty(mApp.permissions))
        {
            ((TextView)findViewById(R.id.lbl_permissions)).setText(mApp.permissions);
        }

        // developer view can be hidden if no data for it
        if (TextUtils.isEmpty(mApp.web) && TextUtils.isEmpty(mApp.email))
        {
            findViewById(R.id.view_developer).setVisibility(View.GONE);
        }

        // fill categories in
        final CategoryBean[] categories = db.appDao().getAllCategoriesForApp(mApp.id);
        final LinearLayout categoryView = (LinearLayout) findViewById(R.id.grp_categories);
        categoryView.removeAllViews();
        for (CategoryBean cb:categories) {
            TextView tv = new TextView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(2,2,2,2);
            tv.setLayoutParams(params);
            tv.setBackgroundResource(R.drawable.rounded_corner);
            tv.setPadding(10,10,10,10);
            tv.setTextColor(getResources().getColor(R.color.album_title));
            tv.setTextSize(14);
            tv.setText(cb.catName);
            categoryView.addView(tv);
        }

        // populate similar apps
        List<ApplicationBean> applicationBeanList;
        RecyclerView viewSameCat = (RecyclerView) findViewById(R.id.rec_view_same_category);
        applicationBeanList = new ArrayList<>();
        AppBeanAdapter adapter = new AppBeanAdapter(mContext, applicationBeanList);
        adapter.setActivity(this); // make this Activity the calling context
        viewSameCat.setItemAnimator(new DefaultItemAnimator());
        viewSameCat.setAdapter(adapter);
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        viewSameCat.setLayoutManager(layoutManager2);
        // use just fist category for now
        AppCollectionDescriptor appCollectionDescriptor = new AppCollectionDescriptor(mContext,"cat:" + categories[0].catName);
        applicationBeanList.clear();
        applicationBeanList.addAll(appCollectionDescriptor.getApplicationBeanList());

        // remove this app itself, because it is similar to itself
        for (ApplicationBean ab:applicationBeanList) {
            if (ab.id.equals(this.mApp.id))
            {
                applicationBeanList.remove(ab);
                break;
            }
        }
        adapter.notifyDataSetChanged();

        // put HTML description in place
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((TextView)findViewById(R.id.lbl_app_desc)).setText(Html.fromHtml(mApp.desc, Html.FROM_HTML_MODE_COMPACT));
        } else {
            ((TextView)findViewById(R.id.lbl_app_desc)).setText(Html.fromHtml(mApp.desc));
        }

        CircularProgressDrawable circularProgressDrawable2 = new CircularProgressDrawable(this);
        circularProgressDrawable2.setStrokeWidth(5f);
        circularProgressDrawable2.setCenterRadius(30f);
        circularProgressDrawable2.start();

        // load icon image (alternatively feature graphic)
        Glide.with(mContext).load("https://f-droid.org/repo/icons-640/"+ mApp.icon).override(192, 192).into((ImageView) findViewById(R.id.img_icon));
        if (mApp.icon != null) {
            if (TextUtils.isEmpty(mApp.featureGraphic))
            {
                Glide.with(mContext)
                        .load("https://f-droid.org/repo/icons-640/"+ mApp.icon).override(192, 192)
                        .into((ImageView) findViewById(R.id.img_header_icon));
            }
            else
            {
                Glide.with(mContext)
                        .load("https://f-droid.org/repo/"+mApp.id+"/"+ mApp.featureGraphic)
                        .into((ImageView) findViewById(R.id.img_header_icon));
            }
        }

        // load screenshots
        final LinearLayout grpScreenshots = (LinearLayout) findViewById(R.id.grp_screenshots);
        if (!TextUtils.isEmpty(mApp.screenshots)) {
            grpScreenshots.removeAllViews();
            for (String ss :
                    mApp.getScreenshotList()) {
                ImageView iv = new ImageView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.width =384;
                lp.height =384;
                iv.setLayoutParams(lp);
                iv.setPadding(5,5,5,5);
                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f);
                circularProgressDrawable.start();
                Glide.with(mContext)
                        .load("https://f-droid.org/repo/"+mApp.id+"/"+ ss)
                        .override(384, 384)
                        .placeholder(circularProgressDrawable)
                        .error(R.drawable.ic_phone_android_black_24dp)
                        .into(iv);
                grpScreenshots.addView(iv);
            }
        }
        else
        {
            grpScreenshots.setVisibility(View.GONE);
        }

        // TODO show changelog from fastlane

        // make the star button useful
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.toggleAppStarred(mContext,mApp.id);
                updateStarButton();
            }
        });

        fabShare = (FloatingActionButton) findViewById(R.id.fab_share);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this intent will open the app in available market apps (users choice - but should be F-Droid)
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+mApp.id));
                startActivity(intent);
            }
        });
        updateStarButton();

        //make the install button useful
        final Button btnInstall = findViewById(R.id.btn_install);
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btnInstall.setEnabled(false);
                btnInstall.setAlpha(.5f);
                btnInstall.setClickable(false);
                AsyncTask.execute(new Runnable() {
                                      @Override
                                      public void run() {
                                          doInBackground("https://f-droid.org/repo/"+mApp.apkname);
                                      }
                                  });
            }
        });

        // make the install button say "update" if already installed
        if (Util.isAppInstalled(mContext, mApp.id))
        {
            if (! mApp.marketversion.equals(Util.getInstalledVersionOfApp(mContext, mApp.id)))
            {
                btnInstall.setText("Update");
            }
            else
            {
                btnInstall.setVisibility(View.GONE);
            }
        }

        // populate the Links-section with further upstream links
        populateUpstreamLink(mApp.source, R.id.tbl_row_source_code);
        populateUpstreamLink(mApp.tracker, R.id.tbl_row_bugtracker);
        populateUpstreamLink(mApp.changelog, R.id.tbl_row_changelog);

        // enable website link
        if (!TextUtils.isEmpty(mApp.web))
        {
            findViewById(R.id.lbl_website).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mApp.web));
                    startActivity(i);
                }
            });
        }

        // enable email link
        if (!TextUtils.isEmpty(mApp.email))
        {
            findViewById(R.id.lbl_email).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",mApp.email, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App "+ mApp.name + " in G-Droid");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mApp.email});
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
        }

        // fill in the ratings
        if (!TextUtils.isEmpty(mApp.metricsJson))
        {
            DecimalFormat df = new DecimalFormat("0.0");
            ((TextView)findViewById(R.id.lbl_rating_gdroid_1)).setText(df.format(mApp.stars) + " ★");
            ((TextView)findViewById(R.id.lbl_rating_gdroid_2)).setText(mApp.metriccount + " G-Droid metrics");

            try {
                JSONObject metrics = new JSONObject(mApp.metricsJson);

                // upstream stars
                boolean hasUpstreamRating = false;
                for (String upstreamSource: new String[]{"Github", "Gitlab"}) {
                    final String metricName = "m_"+upstreamSource.toLowerCase()+"_stars";
                    final int ustars = metrics.optInt(metricName);
                    if (ustars != 0)
                    {
                        ((TextView)findViewById(R.id.lbl_rating_upstream_1)).setText(ustars + " ★");
                        ((TextView)findViewById(R.id.lbl_rating_upstream_2)).setText("stars on "+upstreamSource);
                        hasUpstreamRating = true;
                    }
                }
                if (!hasUpstreamRating)
                {
                    findViewById(R.id.grp_rating_upstream).setVisibility(View.GONE);
                }

                df = new DecimalFormat("0");
                // up-to-date-ness
                final double norm_a24 = metrics.optDouble("age_last_v_24");
                if (! Double.isNaN(norm_a24))
                {
                    ((TextView)findViewById(R.id.lbl_rating_uptodate_1)).setText(df.format(norm_a24*100f) + " %");
                }
                else
                {
                    findViewById(R.id.grp_rating_uptodate).setVisibility(View.GONE);
                }

                // update-cycle
                final double norm_ac = metrics.optDouble("avg_update_frequency");
                if (! Double.isNaN(norm_ac))
                {
                    ((TextView)findViewById(R.id.lbl_rating_releasecycle_1)).setText(df.format(norm_ac) + " days");
                }
                else
                {
                    findViewById(R.id.grp_rating_releasecycle).setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                findViewById(R.id.grp_rating_upstream).setVisibility(View.GONE);
                findViewById(R.id.grp_rating_uptodate).setVisibility(View.GONE);
                findViewById(R.id.grp_rating_releasecycle).setVisibility(View.GONE);
            }

        }
        else
        {
            findViewById(R.id.grp_ratings).setVisibility(View.GONE);
        }

        // was there any action on this view in the intent?
        String action = getIntent().getStringExtra("action");
        if (!TextUtils.isEmpty(action))
        {
            if (action.equals("install"))
            {
                btnInstall.performClick();
            }
        }
    }

    private void repairMissingData() {
        if (TextUtils.isEmpty(mApp.web))
        {
            // Many websites have been removed by the F-Droid maintainers, because they thought, duplicated links are useless
            // or README files are not websites. Indeed MANY free apps use a nicely formatted README.md file as
            // primary website. So it should be shown here. ... Just another messy ad-hoc decision.
            if (! TextUtils.isEmpty(mApp.source))
            {
                mApp.web = mApp.source;
            }
        }

        if (TextUtils.isEmpty(mApp.author))
        {
            if (! TextUtils.isEmpty(mApp.source))
            {
                // fetch author form github url (try catch is just to be safe)
                try {
                    String g = "github.com/";
                    final int from = mApp.source.indexOf(g) + g.length();
                    final int to = mApp.source.indexOf('/', from);
                    String a = mApp.source.substring(from, to);
                    mApp.author = a;
                } catch (Throwable t)
                {
                    // nothing but log
                    Log.e("ADA", "could not fetch author name from github url", t);
                }
            }
        }

    }

    private void populateUpstreamLink(final String appAttribute, int tableRowId) {
        if (!TextUtils.isEmpty(appAttribute))
        {
            final TableRow tblRow = findViewById(tableRowId);
            tblRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(appAttribute));
                    startActivity(i);
                }
            });
        }
        else
        {
            final TableRow tblRow = findViewById(tableRowId);
            tblRow.setVisibility(View.GONE);
        }
    }

    protected Boolean doInBackground(String url) {
        File otaFile;
        otaFile = new File(getApplicationContext().getExternalCacheDir().getAbsolutePath() + File.separator + "last_download" + ".apk");

        boolean flag = true;
        boolean downloading =true;
        try{
            // TODO use another library to download, this google thing sucks
            DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request mRqRequest = new DownloadManager.Request(
                    Uri.parse(url));
            mRqRequest.setTitle(mApp.name).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            mRqRequest.setDestinationUri(Uri.fromFile(otaFile));
            Util.deleteFileIfExist(otaFile.getAbsolutePath());
            long idDownLoad=mManager.enqueue(mRqRequest);
            DownloadManager.Query query = null;
            query = new DownloadManager.Query();
            Cursor c = null;
            if(query!=null) {
                query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
            } else {
                return flag;
            }

            while (downloading) {
                c = mManager.query(query);
                if(c.moveToFirst()) {
                    Log.e ("FLAG","Downloading");
                    int status =c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    if (status==DownloadManager.STATUS_SUCCESSFUL) {
                        Log.e ("FLAG","done");
                        downloading = false;
                        flag=true;

//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(otaFile), "application/vnd.android.package-archive");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
//                        mContext.startActivity(intent);
                        //File file = new File(dir, "App.apk");
                        Uri uri = Uri.fromFile(otaFile);
                        final Intent intent = new Intent();
                        if(Build.VERSION.SDK_INT>=24){
                            try{
                                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                                m.invoke(null);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        if (Build.VERSION.SDK_INT < 14) {
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        } else if (Build.VERSION.SDK_INT < 16) {
                            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(uri);
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            intent.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
                        } else if (Build.VERSION.SDK_INT < 24) {
                            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(uri);
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        } else {
                            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(uri);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        }
                        //startActivityForResult(intent, 0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //startActivityForResult(intent, 0);
                                mContext.startActivity(intent);

                                // own parcelable cursor:
                                // https://stackoverflow.com/questions/17527095/could-not-write-cursorwindow-to-parcel-due-to-error-2147483641/19976499

                                // must use another download lib to prevent this
//                                11-07 06:24:14.995 15992-14156/? E/CursorWindow: Could not allocate
//                                CursorWindow
//                                '/data/data/com.android.providers.downloads/databases/downloads.db' of size 2097152 due to error -12.
//                                11-07 06:24:15.005 15992-14156/? E/DownloadManager: [11771] Failed: android.database.CursorWindowAllocationException: Cursor window allocation of 2048 kb failed. # Open Cursors=723 (# cursors opened by pid 13969=723)
                            }
                        });

                        break;
                    }
                    if (status==DownloadManager.STATUS_FAILED) {
                        Log.e ("FLAG","Fail");
                        downloading = false;
                        flag=false;
                        break;
                    }
                }
            }

            return flag;
        }catch (Exception e) {
            flag = false;
            return flag;
        }
    }

    private void updateStarButton()
    {
        if (Util.isAppstarred(mContext, mApp.id))
        {
            fab.setImageResource(R.drawable.ic_star_white_24dp);
        }
        else
        {
            fab.setImageResource(R.drawable.ic_star_border_white_24dp);
        }
    }
}

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

package org.gdroid.gdroid;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CategoryBean;
import org.gdroid.gdroid.beans.TagBean;
import org.gdroid.gdroid.perm.AppDiff;
import org.gdroid.gdroid.perm.AppSecurityPermissions;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppDetailActivity extends AppCompatActivity implements FetchListener {

    Context mContext;
    ApplicationBean mApp;
    FloatingActionButton fab;
    FloatingActionButton fabShare;
    ProgressBar progressBar;
    Request downloadRequest;

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
            final String lcn = Util.getLocalisedCategoryName(this, cb.catName);
            tv.setText(lcn);
            categoryView.addView(tv);

            // make it clickable
            final String collectionName = "cat:" + cb.catName;
            final String headline = AppCollectionAdapter.getHeadlineForCatOrTag(mContext, collectionName);
            final View.OnClickListener clickListener = AppCollectionAdapter.getOnClickListenerForCatOrTag(collectionName, headline, this);
            tv.setOnClickListener(clickListener);
        }

        // fill tags - the same way as categories
        final TagBean[] tags = db.appDao().getAllTagsForApp(mApp.id);
        for (TagBean tb:tags) {
            TextView tv = new TextView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(2,2,2,2);
            tv.setLayoutParams(params);
            tv.setBackgroundResource(R.drawable.rounded_corner_tag);
            tv.setPadding(10,10,10,10);
            tv.setTextColor(getResources().getColor(R.color.album_title));
            tv.setTextSize(14);
            String tagname = Util.getStringResourceByName(mContext,tb.tagName);
            tv.setText(tagname);
            categoryView.addView(tv);

            // make it clickable
            final String collectionName = "tag:" + tb.tagName;
            final String headline = AppCollectionAdapter.getHeadlineForCatOrTag(mContext, collectionName);
            final View.OnClickListener clickListener = AppCollectionAdapter.getOnClickListenerForCatOrTag(collectionName, headline, this);
            tv.setOnClickListener(clickListener);
        }

        // fill anti-features
        final TextView lblAntiFeatures = findViewById(R.id.lbl_antifeatures);
        if (!TextUtils.isEmpty(mApp.antifeatures))
        {
            String afsString = "";
            final String[] afs = mApp.antifeatures.split(",");
            for (int i = 0 ; i< afs.length ;i++)
            {
                if (i>0)
                {
                    afsString+=", ";
                }
                afsString += Util.getLocalisedAntifeatureDescription(mContext, afs[i]);
            }
            lblAntiFeatures.setText(afsString);
        }
        else
        {
            lblAntiFeatures.setVisibility(View.GONE);
        }

        // populate similar apps
        // use just first category for now
        AppCollectionDescriptor sameCatCollectionDescriptor = new AppCollectionDescriptor(mContext,"cat:" + categories[0].catName);
        populateSimilarAppsView(sameCatCollectionDescriptor,   R.id.lbl_same_category,  R.id.rec_view_same_category);

        AppCollectionDescriptor sameAuthorCollectionDescriptor = new AppCollectionDescriptor(mContext,"author:" + mApp.author);
        populateSimilarAppsView(sameAuthorCollectionDescriptor,  R.id.lbl_same_author,  R.id.rec_view_same_author);


        // put HTML description in place
        if (! TextUtils.isEmpty(mApp.desc)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((TextView) findViewById(R.id.lbl_app_desc)).setText(Html.fromHtml(mApp.desc, Html.FROM_HTML_MODE_COMPACT));
            } else {
                ((TextView) findViewById(R.id.lbl_app_desc)).setText(Html.fromHtml(mApp.desc));
            }
        }
        else
        {
            ((TextView) findViewById(R.id.lbl_app_desc)).setText("");
        }
        CircularProgressDrawable circularProgressDrawable2 = new CircularProgressDrawable(this);
        circularProgressDrawable2.setStrokeWidth(5f);
        circularProgressDrawable2.setCenterRadius(30f);
        circularProgressDrawable2.start();

        // load icon image (alternatively feature graphic)
        GlideApp.with(mContext).load("https://f-droid.org/repo/icons-640/"+ mApp.icon).override(192, 192).into((ImageView) findViewById(R.id.img_icon));
        if (mApp.icon != null) {
            if (TextUtils.isEmpty(mApp.featureGraphic))
            {
                GlideApp.with(mContext)
                        .load("https://f-droid.org/repo/icons-640/"+ mApp.icon).override(192, 192)
                        .into((ImageView) findViewById(R.id.img_header_icon));
            }
            else
            {
                GlideApp.with(mContext)
                        .load("https://f-droid.org/repo/"+mApp.id+"/"+ mApp.featureGraphic)
                        .into((ImageView) findViewById(R.id.img_header_icon));
            }
        }

        // load screenshots
        final LinearLayout grpScreenshots = (LinearLayout) findViewById(R.id.grp_screenshots);
        if (!TextUtils.isEmpty(mApp.screenshots)) {
            grpScreenshots.removeAllViews();
            for (String ss : mApp.getScreenshotList()) {
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
                final String ssUrl;
                if (ss.startsWith("http"))
                {
                    // use absolute links starting with https or http
                    ssUrl = ss;
                }
                else
                {
                    ssUrl = "https://f-droid.org/repo/" + mApp.id + "/" + ss;
                }
                final Drawable errorImg = AppCompatResources.getDrawable(mContext, R.drawable.ic_android_black_24dp);
                GlideApp.with(mContext)
                        .load(ssUrl)
                        .placeholder(circularProgressDrawable)
                        .error(errorImg)
                        .into(iv);
                grpScreenshots.addView(iv);

                final Activity callingActivity = this;
                // make each screenshot clickable
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(callingActivity, ImageActivity.class);
                        myIntent.putExtra("imgUrl", ssUrl);
                        mContext.startActivity(myIntent);
                    }
                });
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
        progressBar = findViewById(R.id.progress_bar);
        final Button btnInstall = findViewById(R.id.btn_install);
        final Activity callerActivity = this;
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadRequest = AppDownloader.download(callerActivity, mApp, true);
            }
        });

        final Button btnLaunch = findViewById(R.id.btn_launch);
        btnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(mApp.id);
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });

        final Button btnCancelDownload = findViewById(R.id.btn_cancel_download);
        btnCancelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadRequest != null)
                {
                    AppDownloader.getFetch(callerActivity).delete(downloadRequest.getId());
                }
            }
        });

        updateInstallStatus(Status.NONE);

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

            // show permissions
            AppDiff appDiff = new AppDiff(this, mApp);
            AppSecurityPermissions perms = new AppSecurityPermissions(this, appDiff.apkPackageInfo);

            final LinearLayout permContainer = findViewById(R.id.ll_permissions_container);
            permContainer.addView(perms.getPermissionsView(AppSecurityPermissions.WHICH_ALL));



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

        db.close();
    }

    private void populateSimilarAppsView(AppCollectionDescriptor appCollectionDescriptor, int headlineLabel, int recViewToFill) {
        List<ApplicationBean> applicationBeanList;
        RecyclerView viewSameCat = (RecyclerView) findViewById(recViewToFill);
        applicationBeanList = new ArrayList<>();
        AppBeanAdapter adapter = new AppBeanAdapter(mContext, applicationBeanList);
        adapter.setActivity(this); // make this Activity the calling context
        viewSameCat.setItemAnimator(new DefaultItemAnimator());
        viewSameCat.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        viewSameCat.setLayoutManager(layoutManager);
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

        TextView lblHeadlineLabel = findViewById(headlineLabel);
        if (applicationBeanList.isEmpty())
        {
            lblHeadlineLabel.setVisibility(View.GONE);
        }
        else
        {
            if (headlineLabel == R.id.lbl_same_author) {
                String a = lblHeadlineLabel.getText().toString();
                if (!TextUtils.isEmpty(mApp.author))
                    a += " (" + mApp.author + ")";
                lblHeadlineLabel.setText(a);
            }
        }

        if (TextUtils.isEmpty(mApp.author) && headlineLabel == R.id.lbl_same_author)
        {
            lblHeadlineLabel.setVisibility(View.GONE);
            viewSameCat.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
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

//    private FetchListener getFetchListener (){
//        return new FetchListener() {
//            @Override
//            public void onAdded(Download download) {
//
//            }
//
//            @Override
//            public void onQueued(Download download, boolean b) {
//
//            }
//
//            @Override
//            public void onWaitingNetwork(Download download) {
//
//            }
//
//            @Override
//            public void onCompleted(Download download) {
//
//            }
//
//            @Override
//            public void onError(Download download, Error error, Throwable throwable) {
//
//            }
//
//            @Override
//            public void onDownloadBlockUpdated(Download download, DownloadBlock downloadBlock, int i) {
//
//            }
//
//            @Override
//            public void onStarted(Download download, List<? extends DownloadBlock> list, int i) {
//
//            }
//
//            @Override
//            public void onProgress(Download download, long l, long l1) {
//
//            }
//
//            @Override
//            public void onPaused(Download download) {
//
//            }
//
//            @Override
//            public void onResumed(Download download) {
//
//            }
//
//            @Override
//            public void onCancelled(Download download) {
//
//            }
//
//            @Override
//            public void onRemoved(Download download) {
//
//            }
//
//            @Override
//            public void onDeleted(Download download) {
//
//            }
//        };
//    }

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

    @Override
    protected void onResume() {
        super.onResume();
        AppDownloader.getFetch(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppDownloader.getFetch(this).removeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDownloader.getFetch(this).close();
    }

    /**
     * sets the area of the look next to the app logo.
     * like: install/update/downloading/uninstall
     * @param status
     */
    public void updateInstallStatus(Status status)
    {
        final Button btnInstall = findViewById(R.id.btn_install);
        final Button btnLaunch = findViewById(R.id.btn_launch);
        final LinearLayout pbh = findViewById(R.id.progress_bar_holder);
        btnInstall.setVisibility(View.GONE);
        btnLaunch.setVisibility(View.GONE);
        pbh.setVisibility(View.GONE);

        switch (status)
        {
            case PAUSED:
            case ADDED:
            case DOWNLOADING:
            case QUEUED: {
                pbh.setVisibility(View.VISIBLE);
                break;
            }

            // this happens when app is not being downloading right now
            default: {
                // make the install button say "upgrade" if already installed
                if (Util.isAppInstalled(mContext, mApp.id))
                {
                    btnLaunch.setVisibility(View.VISIBLE);
                    if (Util.isAppUpdateable(mContext, mApp))
                    {
                        btnInstall.setText(getString(R.string.action_upgrade));
                        btnInstall.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    btnInstall.setText(getString(R.string.action_install));
                    btnInstall.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /*
     * fetch listener implementation
     *
     */

    @Override
    public void onAdded(Download download) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onCancelled(Download download) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onCompleted(Download download) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onDeleted(Download download) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onDownloadBlockUpdated(Download download, @NotNull DownloadBlock downloadBlock, int totalBlocks) {
//        updateInstallStatus(download.getStatus());
    }

    @Override
    public void onError(Download download, Error error, Throwable throwable) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onPaused(Download download) {
        // not pausable
    }

    @Override
    public void onProgress(Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {
        if (download.getExtras().getString("id","").equals(mApp.id)) {
            updateInstallStatus(download.getStatus());
            progressBar.setProgress(download.getProgress());
        }
    }

    @Override
    public void onQueued(Download download, boolean b) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onRemoved(Download download) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onResumed(Download download) {
        // not pausable
    }

    @Override
    public void onStarted(Download download, @NotNull List<? extends DownloadBlock> downloadBlocks, int totalBlocks) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }

    @Override
    public void onWaitingNetwork(Download download) {
        if (download.getExtras().getString("id","").equals(mApp.id))
            updateInstallStatus(download.getStatus());
    }
}

package org.gdroid.gdroid;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CategoryBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AppDetailActivity extends AppCompatActivity {

    Context mContext;
    ApplicationBean mApp;
    FloatingActionButton fab;

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
        toolbar.setTitle(mApp.name);
        toolbarLayout.setTitle(mApp.name);
        ((TextView)findViewById(R.id.lbl_app_name)).setText(mApp.name);
        ((TextView)findViewById(R.id.lbl_app_summary)).setText(mApp.summary);
        ((TextView)findViewById(R.id.lbl_lastupdated)).setText(mApp.lastupdated);
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

        // remove this app itself, becasue it is similar to itself
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


        if (mApp.icon != null) {
            Glide.with(mContext).load("https://f-droid.org/repo/icons-640/"+ mApp.icon).override(192, 192).into((ImageView) findViewById(R.id.img_icon));
            Glide.with(mContext).load("https://f-droid.org/repo/icons-640/"+ mApp.icon).override(192, 192).into((ImageView) findViewById(R.id.img_header_icon));
        }

        // make the star button useful
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.toggleAppStarred(mContext,mApp.id);
                updateStarButton();
            }
        });
        updateStarButton();
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

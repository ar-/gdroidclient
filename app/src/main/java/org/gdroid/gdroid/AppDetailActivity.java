package org.gdroid.gdroid;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;

public class AppDetailActivity extends AppCompatActivity {

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String appId = getIntent().getStringExtra("appId");

        AppDatabase db = AppDatabase.get(getApplicationContext());
        ApplicationBean app = db.appDao().getApplicationBean(appId);
        toolbar.setTitle(app.name);
        toolbarLayout.setTitle(app.name);
        ((TextView)findViewById(R.id.lbl_app_name)).setText(app.name);
        ((TextView)findViewById(R.id.lbl_app_summary)).setText(app.summary);
        ((TextView)findViewById(R.id.lbl_lastupdated)).setText(app.lastupdated);
        ((TextView)findViewById(R.id.lbl_app_author)).setText(app.author);
        ((TextView)findViewById(R.id.lbl_license)).setText(app.license);
        ((TextView)findViewById(R.id.lbl_website)).setText(app.web);
        ((TextView)findViewById(R.id.lbl_email)).setText(app.email);

        // put HTML description in place
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((TextView)findViewById(R.id.lbl_app_desc)).setText(Html.fromHtml(app.desc, Html.FROM_HTML_MODE_COMPACT));
        } else {
            ((TextView)findViewById(R.id.lbl_app_desc)).setText(Html.fromHtml(app.desc));
        }


        if (app.icon != null) {
            Glide.with(mContext).load("https://f-droid.org/repo/icons-640/"+app.icon).override(192, 192).into((ImageView) findViewById(R.id.img_icon));
            Glide.with(mContext).load("https://f-droid.org/repo/icons-640/"+app.icon).override(192, 192).into((ImageView) findViewById(R.id.img_header_icon));
        }


    }
}

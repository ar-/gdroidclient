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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.util.ArrayList;
import java.util.List;

public class AppCollectionActivity extends AppCompatActivity {

    Context mContext;
    boolean isLoading;
    AppBeanAdapter adapter;
    List<ApplicationBean> applicationBeanList;
    final int loadSetpSize = 90;
    int itemsToShow;
    String headline = "";
    String collectionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_collection);
        mContext = getApplicationContext();
        itemsToShow=loadSetpSize;
        isLoading = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // headline
        headline = getIntent().getStringExtra("headline");
        collectionName = getIntent().getStringExtra("collectionName");

        ((CollapsingToolbarLayout)findViewById(R.id.toolbar_layout)).setTitle(headline);

        //populate apps
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int screenWidth = size.x;
        final int gapDp = 5;
        final int cardWidth = 90;
        final int topup = 10;
        final int imgWidth = dpToPx(cardWidth+topup + gapDp);
        int columns = screenWidth / imgWidth;

        RecyclerView viewAppCollection = (RecyclerView) findViewById(R.id.view_app_collection);

        applicationBeanList = new ArrayList<>();
        adapter = new AppBeanAdapter(this, applicationBeanList);

        adapter.setActivity(this); // make this Activity the calling context
        viewAppCollection.setItemAnimator(new DefaultItemAnimator());
        viewAppCollection.setAdapter(adapter);

        if (Util.isListViewPreferred(this))
            columns = 1;

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, columns);
        viewAppCollection.setLayoutManager(mLayoutManager);

        AppCollectionDescriptor appCollectionDescriptor = new AppCollectionDescriptor(mContext,collectionName,itemsToShow);
        applicationBeanList.clear();
        applicationBeanList.addAll(appCollectionDescriptor.getApplicationBeanList());



        // deal with the scrolling
        viewAppCollection.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (applicationBeanList.size() > itemsToShow)
                    return;

                if (!isLoading && newState == 0)
                {
                    // a scrolling action has stopped, have now time to load some more
                    loadMore();
                }
            }
        });
    }

    private void loadMore()
    {
        if (isLoading)
            return;
        isLoading = true;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                itemsToShow += loadSetpSize;
                AppCollectionDescriptor appCollectionDescriptor = new AppCollectionDescriptor(mContext,"cat:"+ headline,loadSetpSize,itemsToShow-loadSetpSize);
                applicationBeanList.addAll(appCollectionDescriptor.getApplicationBeanList());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                    }
                });
            }
        });

    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.tasks.DownloadXmlTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    //private RecyclerView innerRecyclerView;
    //private LinearLayout collectionContent;
    //private HorizontalScrollView inner_scroll_view;
    private AppBeanAdapter adapter;
    private List<ApplicationBean> appBeanList;
    private List<AppCollectionDescriptor> appCollectionDescriptorList;
    private AppCollectionAdapter appCollectionAdapter;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        searchView = findViewById(R.id.search_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(getItemIdForHomeScreenMenuItem(Util.getLastMenuItem(getApplicationContext())));


        final MainActivity activity = this;
        // TODO initial refresh only when DB empty
        //new DownloadXmlTask(activity, appCollectionAdapter).execute("https://f-droid.org/repo/index.xml");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setEnabled(false);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                Snackbar.make(view, "Downloading update ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                new DownloadXmlTask(activity, appCollectionAdapter).execute("https://f-droid.org/repo/index.xml");

            }
        });



    }

    private int getItemIdForHomeScreenMenuItem(String lastMenuItem) {
        switch (lastMenuItem){
            case "home":
                return R.id.navigation_home;
            case "categories":
                return R.id.navigation_categories;
            case "starred":
                return R.id.navigation_starred;
            case "myapps":
                return R.id.navigation_myapps;
            case "search":
                return R.id.navigation_search;
            default:
                return R.id.navigation_home;
        }
    }

    /**
     * Call this to initialize ethe main view with collection cards.
     * These are cards that ontain on row of cards of apps.
     */
    private void setUpCollectionCards() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //innerRecyclerView = (RecyclerView) findViewById(R.id.inner_recycler_view);
        //collectionContent = (LinearLayout) findViewById(R.id.collection_content);
        //inner_scroll_view = (HorizontalScrollView) findViewById(R.id.inner_scroll_view);

//        appDescriptorList = new ArrayList<>();
//        adapter = new AppBeanAdapter(this, appDescriptorList);

        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        removeAllitemDecorations();
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(adapter);

//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        //innerRecyclerView.setLayoutManager(layoutManager);
        //innerRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        //innerRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        //innerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //innerRecyclerView.setAdapter(adapter);
        //innerRecyclerView.setAdapter(appCollectionAdapter);
        appCollectionDescriptorList = new ArrayList<>();
        appCollectionAdapter = new AppCollectionAdapter(this, appCollectionDescriptorList);
        recyclerView.setAdapter(appCollectionAdapter);
    }

    /**
     * call this to set up the main view to show a bunch of apps on a grid with 3 columns
     */
    private void setUpAppCards() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        appBeanList = new ArrayList<>();
        adapter = new AppBeanAdapter(this, appBeanList);

        //inner_recycler_view.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        //inner_recycler_view.addItemDecoration(new MainActivity.GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        removeAllitemDecorations();
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));

//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
//        LinearLayoutManager layoutManager2
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void removeAllitemDecorations()
    {
        while (true)
        {
            if (recyclerView.getItemDecorationCount() >0)
            {
                recyclerView.removeItemDecorationAt(0);
            }
            else
            {
                break;
            }
        }
    }

    private void prepareAppCollections(String screen) {
        final Context context = getApplicationContext();
        if (screen.equals("home"))
        {
            appCollectionDescriptorList.clear();
            AppCollectionDescriptor a = new AppCollectionDescriptor(context, "Newest apps");
            appCollectionDescriptorList.add(a);
            AppCollectionDescriptor a2 = new AppCollectionDescriptor(context, "Recently updated");
            appCollectionDescriptorList.add(a2);
//            AppCollectionDescriptor a3 = new AppCollectionDescriptor(context, "Recommended for you");
//            appCollectionDescriptorList.add(a3);
//            AppCollectionDescriptor a4 = new AppCollectionDescriptor(context, "top rates apps");
//            appCollectionDescriptorList.add(a4);
//            AppCollectionDescriptor a5 = new AppCollectionDescriptor(context, "you might also like");
//            appCollectionDescriptorList.add(a5);
//            AppCollectionDescriptor a6 = new AppCollectionDescriptor(context, "highest rated");
//            appCollectionDescriptorList.add(a6);
//            AppCollectionDescriptor a7 = new AppCollectionDescriptor(context, "popular apps");
//            appCollectionDescriptorList.add(a7);
//            AppCollectionDescriptor a8 = new AppCollectionDescriptor(context, "System");
//            appCollectionDescriptorList.add(a8);
//            AppCollectionDescriptor a9 = new AppCollectionDescriptor(context, "well maintained");
//            appCollectionDescriptorList.add(a9);
        }
        else if (screen.equals("categories"))
        {
            appCollectionDescriptorList.clear();
            AppDatabase db = AppDatabase.get(context);
            final String[] categoryNames = db.appDao().getAllCategoryNames();
            for (String cn:categoryNames) {
                AppCollectionDescriptor ad = new AppCollectionDescriptor(context, "cat:"+cn);
                appCollectionDescriptorList.add(ad);
            }
        }

        appCollectionAdapter.notifyDataSetChanged();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            searchView.setVisibility(View.GONE);
            String screenName = "";
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    screenName = "home";
                    Util.setLastMenuItem(getApplicationContext(), screenName);
                    setUpCollectionCards();
                    prepareAppCollections(screenName);
                    return true;
                case R.id.navigation_categories:
                    screenName = "categories";
                    Util.setLastMenuItem(getApplicationContext(), screenName);
                    setUpCollectionCards();
                    prepareAppCollections(screenName);
                    return true;
                case R.id.navigation_starred:
                    screenName = "starred";
                    Util.setLastMenuItem(getApplicationContext(), screenName);
                    setUpAppCards();
                    AppCollectionDescriptor appCollectionDescriptor = new AppCollectionDescriptor(getApplicationContext(),screenName);
                    appBeanList.clear();
                    appBeanList.addAll(appCollectionDescriptor.getApplicationBeanList());
                    adapter.notifyDataSetChanged();
//                    Intent myIntent = new Intent(getApplicationContext(), AppCollectionActivity.class);
//                    myIntent.putExtra("collectionName", "starred");
//                    myIntent.putExtra("headline", "starred");
//                    startActivity(myIntent);
                    return true;
                case R.id.navigation_myapps:
                    screenName = "myapps";
                    Util.setLastMenuItem(getApplicationContext(), screenName);
                    setUpAppCards();
                    final String finalScreenName = screenName;
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppCollectionDescriptor myAppsCollectionDescriptor = new AppCollectionDescriptor(getApplicationContext(),finalScreenName);
                            appBeanList.clear();
                            appBeanList.addAll(myAppsCollectionDescriptor.getApplicationBeanList());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                    return true;
                case R.id.navigation_search:
                    screenName = "search";
                    Util.setLastMenuItem(getApplicationContext(), screenName);
                    searchView.setVisibility(View.VISIBLE);
                    searchView.setIconifiedByDefault(false);
//                    searchView.requestFocus();
//                    hideSoftKeyboard(searchView);
                    showSoftKeyboard(searchView);
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    setUpAppCards();
//                    final CharSequence query = searchView.getQuery();
//                    AppCollectionDescriptor acd = new AppCollectionDescriptor(getApplicationContext(),"search:"+query,10);
//                    appBeanList.clear();
//                    appBeanList.addAll(acd.getApplicationBeanList());

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            final CharSequence query = searchView.getQuery();
                            AppCollectionDescriptor acd = new AppCollectionDescriptor(getApplicationContext(),"search:"+query,2000);
                            appBeanList.clear();
                            appBeanList.addAll(acd.getApplicationBeanList());
                            adapter.notifyDataSetChanged();
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            final CharSequence query = searchView.getQuery();
                            AppCollectionDescriptor acd = new AppCollectionDescriptor(getApplicationContext(),"search:"+query,20);
                            appBeanList.clear();
                            appBeanList.addAll(acd.getApplicationBeanList());
                            adapter.notifyDataSetChanged();
                            return false;
                        }
                    });
//                    Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
//                    searchIntent.putExtra("collectionName", "starred");
//                    searchIntent.putExtra("headline", "starred");
//                    startActivity(searchIntent);
                    return true;
            }
            return false;
        }
    };

    public void showSoftKeyboard(View view) {
//        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
//        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        view.requestFocus();
//        inputMethodManager.showSoftInput(view, 0);
//        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            //myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        if (id == R.id.nav_my_apps) {
            navigation.setSelectedItemId(getItemIdForHomeScreenMenuItem("myapps"));
//        } else if (id == R.id.nav_gallery) {
//        } else if (id == R.id.nav_slideshow) {
//        } else if (id == R.id.nav_manage) {
//        } else if (id == R.id.nav_share) {
//        } else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}

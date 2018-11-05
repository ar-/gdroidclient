package org.gdroid.gdroid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.tasks.DownloadXmlTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    //private RecyclerView innerRecyclerView;
    //private LinearLayout collectionContent;
    //private HorizontalScrollView inner_scroll_view;
//    private AppBeanAdapter adapter;
//    private List<ApplicationBean> appDescriptorList;
    private List<AppCollectionDescriptor> appCollectionDescriptorList;
    private AppCollectionAdapter appCollectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        prepareAppCollections("home");

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
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    prepareAppCollections("home");
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_categories:
                    prepareAppCollections("categories");
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_starred:
                    prepareAppCollections("starred");
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_myapps:
                    prepareAppCollections("myapps");
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_search:
                    prepareAppCollections("search");
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
            //Intent myIntent = new Intent(this, TempTabActivity.class);
            //myIntent.putExtra("key", value); //Optional parameters
            //this.startActivity(myIntent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

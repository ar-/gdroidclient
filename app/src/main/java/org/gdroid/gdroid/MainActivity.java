package org.gdroid.gdroid;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import org.gdroid.gdroid.tasks.DownloadXmlTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    //private RecyclerView innerRecyclerView;
    //private LinearLayout collectionContent;
    //private HorizontalScrollView inner_scroll_view;
//    private AppGridAdapter adapter;
//    private List<ApplicationBean> appDescriptorList;
    private List<AppCollectionDescriptor> appCollectionDescriptorList;
    private CollectionGridAdapter appCollectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Downloading update ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                new DownloadXmlTask().execute("http://stackoverflow.com/feeds/tag?tagnames=android&sort=newest");

            }
        });

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
//        adapter = new AppGridAdapter(this, appDescriptorList);

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
        appCollectionAdapter = new CollectionGridAdapter(this, appCollectionDescriptorList);
        recyclerView.setAdapter(appCollectionAdapter);



//        prepareAlbums();


        prepareAppCollections();


    }


    private void prepareAppCollections() {
        AppCollectionDescriptor a = new AppCollectionDescriptor("newest apps");
        appCollectionDescriptorList.add(a);
        AppCollectionDescriptor a2 = new AppCollectionDescriptor("recently updated");
        appCollectionDescriptorList.add(a2);
        AppCollectionDescriptor a3 = new AppCollectionDescriptor("recommended for you");
        appCollectionDescriptorList.add(a3);
        AppCollectionDescriptor a4 = new AppCollectionDescriptor("top rates apps");
        appCollectionDescriptorList.add(a4);
        AppCollectionDescriptor a5 = new AppCollectionDescriptor("you might also like");
        appCollectionDescriptorList.add(a5);
        AppCollectionDescriptor a6 = new AppCollectionDescriptor("highest rated");
        appCollectionDescriptorList.add(a6);
        AppCollectionDescriptor a7 = new AppCollectionDescriptor("popular apps");
        appCollectionDescriptorList.add(a7);
        AppCollectionDescriptor a8 = new AppCollectionDescriptor("many forks");
        appCollectionDescriptorList.add(a8);
        AppCollectionDescriptor a9 = new AppCollectionDescriptor("well maintained");
        appCollectionDescriptorList.add(a9);
    }
    /**
     * Adding few albums for testing
     */
//    private void prepareAlbums() {
//        int[] covers = new int[]{
//                R.drawable.ic_menu_camera,
//                R.drawable.a1,
//                R.drawable.a1,
//                R.drawable.album1,
//                R.drawable.a1,
//                R.drawable.album1,
//                R.drawable.a1,
//                R.drawable.a1,
//                R.drawable.album1,
//                R.drawable.album1,
//                R.drawable.album1};
////        R.drawable.album1,
////                R.drawable.album2,
////                R.drawable.album3,
////                R.drawable.album4,
////                R.drawable.album5,
////                R.drawable.album6,
////                R.drawable.album7,
////                R.drawable.album8,
////                R.drawable.album9,
////                R.drawable.album10,
////                R.drawable.album11};
//
//        ApplicationBean a = new ApplicationBean("True Romance", 4.5f, covers[0]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Xscpae", 2, covers[1]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Maroon 5", 4.5f, covers[2]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Born to Die", 4.5f, covers[3]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Honeymoon", 4.5f, covers[4]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("I Need a Doctor", 1, covers[5]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Loud", 4.5f, covers[6]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Legend", 4.5f, covers[7]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Hello", 4, covers[8]);
//        appDescriptorList.add(a);
//
//        a = new ApplicationBean("Greatest Hits", 2, covers[9]);
//        appDescriptorList.add(a);
//
//        adapter.notifyDataSetChanged();
//    }

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
            Intent myIntent = new Intent(this, TempTabActivity.class);
            //myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);
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
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}

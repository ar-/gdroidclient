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
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.util.ArrayList;
import java.util.List;

public class AppCollectionAdapter extends RecyclerView.Adapter<AppCollectionAdapter.MyViewHolder> {

    private Context mContext;
    private List<AppCollectionDescriptor> appCollectionDescriptorList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        TextView moreButton;
        RelativeLayout headlineContainer;
        public RecyclerView inner_recycler_view;

        private AppBeanAdapter adapter;
        private List<ApplicationBean> applicationBeanList;


        public MyViewHolder(View view) {
            super(view);
            headlineContainer = (RelativeLayout) view.findViewById(R.id.collection_headline_container);
            title = (TextView) view.findViewById(R.id.collection_headline);
            moreButton = (TextView) view.findViewById(R.id.more_button);
            inner_recycler_view = (RecyclerView) view.findViewById(R.id.inner_recycler_view);

            applicationBeanList = new ArrayList<>();
            adapter = new AppBeanAdapter(mContext, applicationBeanList);

            inner_recycler_view.setItemAnimator(new DefaultItemAnimator());

            inner_recycler_view.setAdapter(adapter);

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

            inner_recycler_view.setLayoutManager(layoutManager);
        }

        /**
         * Converting dp to pixel
         */
        private int dpToPx(int dp) {
            Resources r = mContext.getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        }

    }

    public AppCollectionAdapter(Context mContext, List<AppCollectionDescriptor> appCollectionDescriptorList) {
        this.mContext = mContext;
        this.appCollectionDescriptorList = appCollectionDescriptorList;
    }

    public Context getmContext() {
        return mContext;
    }

    public List<AppCollectionDescriptor> getAppCollectionDescriptorList() {
        return appCollectionDescriptorList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AppCollectionDescriptor appCollectionDescriptor = appCollectionDescriptorList.get(position);
        final String collectionName = appCollectionDescriptor.getName();
        final String headline = getHeadlineForCatOrTag(mContext, collectionName);
        final View.OnClickListener clickListener = getOnClickListenerForCatOrTag(collectionName, headline, (Activity)mContext);

        holder.title.setText(headline);
        holder.applicationBeanList.clear();
        holder.applicationBeanList.addAll(appCollectionDescriptor.getApplicationBeanList());
        holder.adapter.notifyDataSetChanged();

        holder.moreButton.setOnClickListener(clickListener);
        holder.headlineContainer.setOnClickListener(clickListener);
        holder.title.setOnClickListener(clickListener);

    }

    public static String getHeadlineForCatOrTag(Context mContext, String collectionName) {
        final String headline;
        if (collectionName.startsWith("cat:"))
        {
            String catName = collectionName.replace("cat:","");
            headline = Util.getLocalisedCategoryName(mContext, catName);
        }
        else if (collectionName.startsWith("tag:"))
        {
            String tagName = collectionName.replace("tag:","");
            headline = Util.getStringResourceByName(mContext, tagName);
        }
        else
        {
            headline = collectionName;
        }
        return headline;
    }

    @NonNull
    public static View.OnClickListener getOnClickListenerForCatOrTag(final String collectionName, final String headline, final Activity activity) {
        return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(activity, AppCollectionActivity.class);
                    myIntent.putExtra("collectionName", collectionName);
                    myIntent.putExtra("headline", headline);
                    activity.startActivity(myIntent);
                }
            };
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return appCollectionDescriptorList.size();
    }
}
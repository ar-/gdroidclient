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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.gdroid.gdroid.beans.ApplicationBean;

import java.util.List;

public class AppBeanAdapter extends RecyclerView.Adapter<AppBeanAdapter.MyViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<ApplicationBean> applicationBeanList;

    public int getCount() {
        return applicationBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public String appId;
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);

            //final Activity activity = (Activity) mContext;
            final Activity activity = getActivity();
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(activity, AppDetailActivity.class);
                    myIntent.putExtra("appId", appId);
                    activity.startActivity(myIntent);

                }
            });


        }
    }

    public AppBeanAdapter(Context mContext, List<ApplicationBean> applicationBeanList) {
        this.mContext = mContext;
        this.applicationBeanList = applicationBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ApplicationBean applicationBean = applicationBeanList.get(position);
        if (applicationBean == null)
            return;
        holder.appId = applicationBean.id;
        holder.title.setText(applicationBean.name);
        holder.count.setText(applicationBean.stars + " ★");

        // loading applicationBean cover using Glide library
        //Glide.with(mContext).load(applicationBean.getThumbnail()).into(holder.thumbnail);
        //Glide.with(mContext).load()
//        holder.thumbnail.setImageDrawable();
        if (applicationBean.icon != null) {
            Glide.with(mContext).load("https://f-droid.org/repo/icons-640/"+applicationBean.icon).override(192, 192).into(holder.thumbnail);
        }
//        new DownloadImageTask(holder.thumbnail)
//                .execute("https://f-droid.org/repo/icons-640/community.fairphone.fplauncher3.10.png");


        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     * @param holder
     */
    private void showPopupMenu(MyViewHolder holder) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, holder.overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new AppCardPopupMenuItemClickListener(holder));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class AppCardPopupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private final MyViewHolder holder;

        public AppCardPopupMenuItemClickListener(MyViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Util.starApp(mContext,holder.appId);
                    return true;
                case R.id.action_play_next:
                    Intent myIntent = new Intent(mContext, AppDetailActivity.class);
                    myIntent.putExtra("appId", holder.appId);
                    myIntent.putExtra("action", "install");
                    mContext.startActivity(myIntent);
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return applicationBeanList.size();
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public Activity getActivity() {
        Activity c = Util.getActivity(mContext);
        if (c!=null)
            return c;
        return mActivity;
    }
}
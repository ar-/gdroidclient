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
import android.os.AsyncTask;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.gdroid.gdroid.beans.AppCollectionDescriptor;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.text.DecimalFormat;
import java.util.List;

public class AppBeanAdapter extends RecyclerView.Adapter<AppBeanAdapter.MyViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<ApplicationBean> applicationBeanList;

    public int getCount() {
        return applicationBeanList.size();
    }

    public List<ApplicationBean> getAppBeanList() {
        return applicationBeanList;
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
        final ApplicationBean applicationBean = applicationBeanList.get(position);
        if (applicationBean == null)
            return;
        holder.appId = applicationBean.id;
        holder.title.setText(applicationBean.name);
        DecimalFormat df = new DecimalFormat("0.0");
        holder.count.setText(df.format(applicationBean.stars) + " ★");

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if (applicationBean.icon != null) {
            try
            {
                Glide.with(mContext)
                        .load("https://f-droid.org/repo/icons-640/"+applicationBean.icon)
                        .override(192, 192)
                        .placeholder(circularProgressDrawable)
                        .error(R.drawable.ic_android_black_24dp)
                        .into(holder.thumbnail);
            }
            catch (Throwable t)
            {
                Log.e("ABA", "error while glide sets the app logo", t);
            }
        }
//        new DownloadImageTask(holder.thumbnail)
//                .execute("https://f-droid.org/repo/icons-640/community.fairphone.fplauncher3.10.png");


        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder);
            }
        });

        // show an updateable logo on the card, if there is an update for this app
        // do in background to make it faster
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String installedVersionOfApp = Util.getInstalledVersionOfApp(mContext, applicationBean.id);
                int drawableToBeSet = R.drawable.ic_more_vert_black_24dp;
                if (Util.isAppUpdateable(mContext, applicationBean)) {
                    drawableToBeSet = R.drawable.ic_update_green_24dp;
                }
                holder.overflow.setImageResource(drawableToBeSet);
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
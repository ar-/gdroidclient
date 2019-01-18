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
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.gdroid.gdroid.beans.ApplicationBean;

import java.text.DecimalFormat;
import java.util.List;

public class AppBeanAdapter extends RecyclerView.Adapter<AppBeanAdapter.MyViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<ApplicationBean> mApplicationBeanList;
    private final boolean mEnforceSmallCard;

    public AppBeanAdapter(Context context, List<ApplicationBean> applicationBeanList, boolean enforceSmallCard) {
        this.mContext = context;
        this.mApplicationBeanList = applicationBeanList;
        this.mEnforceSmallCard = enforceSmallCard;
    }

    public AppBeanAdapter(Context context, List<ApplicationBean> applicationBeanList) {
        this(context, applicationBeanList, false);
    }


    public int getCount() {
        return mApplicationBeanList.size();
    }

    public List<ApplicationBean> getAppBeanList() {
        return mApplicationBeanList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public String appId;
        public TextView title, count, desc ;
        public ImageView thumbnail, overflow;
        private final ImageView starOnCard;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            desc = (TextView) view.findViewById(R.id.lbl_desc);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            starOnCard = (ImageView) view.findViewById(R.id.img_star_on_card);

            mActivity = getActivity();
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(mActivity, AppDetailActivity.class);
                    myIntent.putExtra("appId", appId);
                    mActivity.startActivity(myIntent);

                }
            });


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int layout = !mEnforceSmallCard && Util.isListViewPreferred(mContext) ? R.layout.app_list_card : R.layout.app_card;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (position >= mApplicationBeanList.size())
            return; // this can happen since list gets filled async, if next fill happens too quick
        final ApplicationBean applicationBean = mApplicationBeanList.get(position);
        if (applicationBean == null)
            return;
        holder.appId = applicationBean.id;
        holder.title.setText(applicationBean.name);
        DecimalFormat df = new DecimalFormat("0.0");
        holder.count.setText(df.format(applicationBean.stars) + " ★");

        if (holder.desc != null)
        {
            String plain = "";
            if (!TextUtils.isEmpty(applicationBean.desc))
                plain = Html.fromHtml( applicationBean.desc).toString();

            String summary = "";
            if (!TextUtils.isEmpty(applicationBean.summary))
                summary = applicationBean.summary;

            SpannableStringBuilder str = new SpannableStringBuilder(summary + " - " + plain);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, summary.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.desc.setText(str);
        }

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if (applicationBean.icon != null) {
            try
            {
                final Drawable errorImg = AppCompatResources.getDrawable(mContext, R.drawable.ic_android_black_24dp);
                GlideApp.with(mContext)
                        .load("https://f-droid.org/repo/icons-640/"+applicationBean.icon)
                        .override(192, 192)
                        .placeholder(circularProgressDrawable)
                        .error(errorImg)
                        .into(holder.thumbnail);
            }
            catch (Throwable t)
            {
                Log.e("ABA", "error while glide sets the app logo", t);
            }
        }



        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder, applicationBean);
            }
        });

        // show an updateable logo on the card, if there is an update for this app
        // do in background to make it faster
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final int drawableToBeSet;
                if (Util.isAppUpdateable(mContext, applicationBean))
                {
                    drawableToBeSet = R.drawable.ic_update_green_24dp;
                }
                else
                {
                    drawableToBeSet = R.drawable.ic_more_vert_black_24dp;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.overflow.setImageResource(drawableToBeSet);
                    }
                });
            }
        });

        // if the app is starred show a star, also in background, since it will be slow to do this for each app
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final boolean isStarred = Util.isAppstarred(mContext, applicationBean.id);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.starOnCard.setVisibility(isStarred ? View.VISIBLE : View.GONE);
                    }
                });
            }
        });


    }

    /**
     * Showing popup menu when tapping on 3 dots
     * @param holder
     */
    private void showPopupMenu(MyViewHolder holder, ApplicationBean app) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, holder.overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_on_card, popup.getMenu());
        popup.setOnMenuItemClickListener(new AppCardPopupMenuItemClickListener(holder));
        // show hide or unhide option (#62)
        if (app.isHidden)
            popup.getMenu().removeItem(R.id.action_hide);
        else
            popup.getMenu().removeItem(R.id.action_unhide);
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
                case R.id.action_hide:
                    Util.hideApp(mContext,holder.appId);
                    return true;
                case R.id.action_unhide:
                    Util.unhideApp(mContext,holder.appId);
                    return true;
                case R.id.action_install:
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
        return mApplicationBeanList.size();
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
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

public class AppGridAdapter extends RecyclerView.Adapter<AppGridAdapter.MyViewHolder> {

    private Context mContext;
    private List<ApplicationBean> applicationBeanList;

    public int getCount() {
        return applicationBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);

            final Activity activity = (Activity) mContext;
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(activity, AppDetailActivity.class);
                    //myIntent.putExtra("key", value); //Optional parameters
                    activity.startActivity(myIntent);

                }
            });


        }
    }

    public AppGridAdapter(Context mContext, List<ApplicationBean> applicationBeanList) {
        this.mContext = mContext;
        this.applicationBeanList = applicationBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ApplicationBean applicationBean = applicationBeanList.get(position);
        holder.title.setText(applicationBean.getName());
        holder.count.setText(applicationBean.getStars() + " ★");

        // loading applicationBean cover using Glide library
        //Glide.with(mContext).load(applicationBean.getThumbnail()).into(holder.thumbnail);
        //Glide.with(mContext).load()
//        holder.thumbnail.setImageDrawable();

        Glide.with(mContext).load("https://f-droid.org/repo/icons-640/community.fairphone.fplauncher3.10.png").into(holder.thumbnail);
//        new DownloadImageTask(holder.thumbnail)
//                .execute("https://f-droid.org/repo/icons-640/community.fairphone.fplauncher3.10.png");


        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
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
        return applicationBeanList.size();
    }
}
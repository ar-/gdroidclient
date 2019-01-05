/*
 * G-Droid
 * Copyright (C) 2018 Andreas Redmer <ar-gdroid@abga.be>
 * <p/>
 * BARIA - Backup And Restore Installed Apps
 * Copyright (C) 2016  vishnu@easwareapps.com
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 package com.easwareapps.baria;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gdroid.gdroid.AppBeanAdapter;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.io.File;
import java.util.ArrayList;

// TODO delete this file
public class SavedAppsAdapter
//        extends RecyclerView.Adapter<SavedAppsAdapter.ViewHolder>
{
//
//
//    boolean selectionStarted = false;
//    Context context;
//    ArrayList<PInfo> appsDetails;
//    LruCache<String, Bitmap> cache;
//    int iconSize;
//    static SavedAppsAdapter instance = null;
//    static RecyclerView rv;
//    ActionMode actionMode;
//    static FragmentActivity activity;
////    BariaPref pref;
//
//    public static SavedAppsAdapter getInstance(ArrayList<PInfo> apps, Context context,
//                                                   int iconSize, RecyclerView rv1, FragmentActivity fa) {
//        activity = fa;
//        rv = rv1;
//        if(instance == null) {
//            instance = new SavedAppsAdapter();
//        }
//        instance.appsDetails = apps;
//        instance.iconSize = iconSize;
//        instance.context = context;
////        instance.pref = BariaPref.getInstance(context);
//
//        return instance;
//    }
//
//    public SavedAppsAdapter(){
//
//
//        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
//        int cacheSize = maxMemory/8;
//        cache = new LruCache<String, Bitmap>(cacheSize){
//
//            @Override
//            protected int sizeOf(String key, Bitmap value) {
//                return value.getRowBytes() - value.getHeight();
//            }
//
//        };
//
//        rv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                selectionStarted = true;
//                return true;
//            }
//        });
//
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        ImageView icon = null;
//        TextView name = null;
//        View mainView;
//        public ViewHolder(View view){
//            super(view);
//            mainView = view;
////            icon = (ImageView)view.findViewById(R.id.app_icon);
////            name = (TextView)view.findViewById(R.id.app_name);
//            mainView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    view.setSelected(true);
//
//                }
//            });
//            mainView.setOnClickListener(this);
//            mainView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    if(!selectionStarted) {
//                        selectionStarted = true;
//                        actionMode = activity.startActionMode(mActionModeCallback);
//                    }
//                    return false;
//                }
//            });
//        }
//
//
//        @Override
//        public void onClick(View view) {
//
//            if(!selectionStarted) {
//                ArrayList<PInfo> app = new ArrayList<>();
//                app.add(appsDetails.get(getAdapterPosition()));
//                installAPK(app);
//                return;
//            }
//            appsDetails.get(getAdapterPosition()).selected = ! appsDetails.get(getAdapterPosition()).selected;
//            view.setSelected(appsDetails.get(getAdapterPosition()).selected);
//            if(appsDetails.get(getAdapterPosition()).selected) {
////                view.setBackgroundColor(pref.getSelectionColor());
//            } else {
////                view.setBackgroundColor(pref.getNormalColor());
//            }
//
//
//        }
//    }
//
//    public android.view.ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {
//        @Override
//        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
//
//            try {
//                FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
////                fab.setImageResource(R.mipmap.ic_install);
////                fab.setVisibility(View.VISIBLE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            MenuInflater inflater = activity.getMenuInflater();
////            inflater.inflate(R.menu.selection_menu_saved_apps, menu);
//
////            MenuItem share = menu.findItem(R.id.menu_share);
////            share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////                @Override
////                public boolean onMenuItemClick(MenuItem menuItem) {
////                    shareApps();
////                    return false;
////                }
////            });
//
////            MenuItem copy = menu.findItem(R.id.menu_install);
////            copy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////                @Override
////                public boolean onMenuItemClick(MenuItem menuItem) {
////                    installAPK(null);
////                    return false;
////                }
////            });
//
////            final MenuItem menuSelectAll = menu.findItem(R.id.menu_select_all);
////            menuSelectAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////                @Override
////                public boolean onMenuItemClick(MenuItem menuItem) {
////                    selectAll();
////                    return false;
////                }
////            });
//
//
//            return true;
//        }
//
//        @Override
//        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
//            return false;
//        }
//
//        @Override
//        public void onDestroyActionMode(android.view.ActionMode mode) {
//
//            selectionStarted = false;
//            removeAllSelection();
//            FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
////            fab.setVisibility(View.GONE);
//            //fab.startActionMode(null);
//
//        }
//
//        @Override
//        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
//            return false;
//        }
//    };
//
//    private void selectAll() {
//        for(PInfo pkg: appsDetails) {
//            pkg.selected = true;
//        }
//        notifyDataSetChanged();
//    }
//
//    private void removeAllSelection() {
//        for(PInfo pkg: appsDetails) {
//            pkg.selected = false;
//        }
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_line, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//
//
//
//        holder.name.setText(appsDetails.get(position).appname);
//        final String imageKey = String.valueOf(appsDetails.get(position).pname);
//        final Bitmap bitmap = cache.get(imageKey);
//        holder.icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
//        holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (bitmap != null) {
//            holder.icon.setImageBitmap(bitmap);
//        } else {
//            Resources resource = context.getResources();
////            BitmapManager bm = new BitmapManager(holder.icon, resource, cache);
////            bm.setPackageName(appsDetails.get(position).icon);
////            final BitmapManager.AsyncDrawable asyncDrawable =
////                    new BitmapManager.AsyncDrawable(resource, null, bm);
////            holder.icon.setImageDrawable(asyncDrawable);
////            bm.execute(1);
//        }
//        holder.mainView.setSelected(appsDetails.get(position).selected);
//        if(appsDetails.get(position).selected) {
////            holder.mainView.setBackgroundColor(pref.getSelectionColor());
//        } else {
////            holder.mainView.setBackgroundColor(pref.getNormalColor());
//        }
//
//
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return appsDetails.size();
//    }
//
//
//    AutoRootAppInstallTask installTask;
//    public void installAPK(final ArrayList<ApplicationBean> apps){
//
//        boolean singleApk = false;
//        if(apps != null){
//            installTask = new AutoRootAppInstallTask(context, apps);
//            installTask.setSingleApk();
//            singleApk = true;
//        }
//        else
//            installTask = new AutoRootAppInstallTask(context, appsDetails);
//        if (installTask.isRooted()) {
//            AlertDialog.Builder questionDialog = new AlertDialog.Builder(context);
//            questionDialog.setTitle(R.string.confirmation)
//                    .setMessage(R.string.use_root)
//            .setIcon(R.mipmap.ic_launcher);
//            questionDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    installTask.useRootPrivilege();
//                    installTask.execute();
//
//                }
//            });
//
//            questionDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                    installAppsManually(apps);
//
//                }
//            });
//
//            questionDialog.create().show();
//            return;
//        }
//
//        installAppsManually(apps);
//
//
//    }
//
//    private void installAppsManually(ArrayList<PInfo> apps) {
//        ArrayList<String> apks = new ArrayList<>();
//        Intent intent = new Intent(context, ManualAppInstallActivity.class);
//        if(apps != null) {
//            apks.add(apps.get(0).apk);
//
//        }else{
//            for(int i=0;i<appsDetails.size(); i++) {
//                if(appsDetails.get(i).selected)
//                    apks.add(appsDetails.get(i).apk);
//            }
//        }
//        intent.putExtra("apps", apks);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//
//    }
//
//    public void closeActionMode() {
//        if(actionMode != null) {
//            actionMode.finish();
//        }
//    }
//
//    private void shareApps() {
//        ArrayList<Uri> files = new ArrayList<>();
//        int noFiles = 0;
//        for (PInfo pkg: appsDetails) {
//            if(pkg.selected) {
//                files.add(Uri.fromFile(new File(pkg.apk)));
//                noFiles++;
//            }
//        }
//        if(noFiles > 0 ) {
//
//            Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
//            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
//            share.setType("application/vnd.android.package-archive");
////            share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.shared_using_baria, "\n\nhttp://baria.easwareapps.com/"));
//            context.startActivity(share);
//        }else {
////            Snackbar.make(activity.findViewById(R.id.main_content),
////                    context.getString(R.string.nothing_to_share), Snackbar.LENGTH_LONG)
////                    .setAction("Action", null).show();
//        }
//    }



}

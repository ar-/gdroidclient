/*
 * G-Droid
 * Copyright (C) 2018,2019 Andreas Redmer <ar-gdroid@abga.be>
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

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import org.gdroid.gdroid.R;
import org.gdroid.gdroid.installer.DefaultInstaller;

import java.util.ArrayList;

public class ManualAppInstallActivity extends AppCompatActivity {

    int index = 0;
    int total = 0;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    ArrayList<String> apps;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        try {
            apps = (ArrayList<String>) intent.getSerializableExtra("apps");
            total = apps.size();

        }catch (Exception e){

        }
        if(apps.size() > 0) {
            installAppManually(apps.get(0));
        }

        super.onCreate(savedInstanceState);
    }

    private void installAppManually(String file) {
        PackageManager pm = getPackageManager();
        PackageInfo p = pm.getPackageArchiveInfo(file, 0);
        String appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
        showNotification(getResources().getString(R.string.installing_details, index, total),
                getResources().getString(R.string.installing, appname));

        index ++;

        DefaultInstaller installer = new DefaultInstaller();
        installer.installApp(this, file, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(index == total) {
            showNotification(getResources().getString(R.string.installation_finished),
                    getResources().getString(R.string.installed_details, index));

            //update UI
            final ComponentName callingActivity = getCallingActivity();
            final Activity parent = getParent();
            final Context baseContext = getBaseContext();
//            if (context instanceof MainActivity)
//            {
//                final MainActivity ma = (MainActivity) context;
//                ma.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ma.updateCurrentView();
//                    }
//                });
//            }

            finish();
        }else{
            installAppManually(apps.get(index));
        }
        super.onResume();
    }

    private void showNotification(String title, String desc) {
        mNotifyManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "30009";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mNotifyManager.createNotificationChannel(mChannel);
            mBuilder = new NotificationCompat.Builder(this, chanel_id);
        } else {
            mBuilder = new NotificationCompat.Builder(this);
        }

        mBuilder.setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        mNotifyManager.notify(14099, mBuilder.build());
    }
}

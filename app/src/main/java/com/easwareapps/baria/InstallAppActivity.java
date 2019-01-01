/**
 ************************************** ॐ ***********************************
 ***************************** लोकाः समस्ताः सुखिनो भवन्तु॥**************************
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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.easwareapps.baria.PInfo;

import org.gdroid.gdroid.R;

import java.io.File;
import java.util.ArrayList;

/**
 * ************************************* ॐ ***********************************
 * **************************** लोकाः समस्ताः सुखिनो भवन्तु॥**************************
 * <p/>
 * EA-BulkReinstaller
 * Copyright (C) 2016  vishnu
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
public class InstallAppActivity extends AppCompatActivity {


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
        String appname = p.applicationInfo.loadLabel(getPackageManager()).toString();;
        Drawable icon =  p.applicationInfo.loadIcon(getPackageManager());
        showNotification(getResources().getString(R.string.installing_details, index, total),
                getResources().getString(R.string.installing, appname) , getBitmap(icon));

        index ++;
        Uri uri = Uri.fromFile(new File(file));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(index == total) {
            showNotification(getResources().getString(R.string.installation_finished),
                    getResources().getString(R.string.installed_details, index),
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_install));
            finish();
        }else{
            installAppManually(apps.get(index));
        }
        super.onResume();
    }

    private void showNotification(String title, String desc,
                                  Bitmap bitmap) {


        mNotifyManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap);
        mNotifyManager.notify(14099, mBuilder.build());
    }

    private Bitmap getBitmap(Drawable icon) {
        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);
        return bitmap;
    }
}

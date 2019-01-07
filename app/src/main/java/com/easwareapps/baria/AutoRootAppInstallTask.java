/*
 * G-Droid
 * Copyright (C) 2018 Andreas Redmer <ar-gdroid@abga.be>
` * <p/>
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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.gdroid.gdroid.AppDownloader;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class AutoRootAppInstallTask extends AsyncTask<Void, ApplicationBean, Void> {

    ArrayList<ApplicationBean> packages;
    int totalCount = 0;
    int current = 0;
    int error=  0;
    Context context;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public AutoRootAppInstallTask(Context ctx, ArrayList<ApplicationBean> packages) {
        this.packages = packages;
        this.context = ctx;
        for (ApplicationBean pkg: packages) {
                totalCount++;
        }
    }

    @Override
    protected void onPreExecute() {
        //Create Progressbar
        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
            mBuilder = new NotificationCompat.Builder(context, chanel_id);
        } else {
            mBuilder = new NotificationCompat.Builder(context);
        }

        mBuilder.setContentTitle(context.getString(R.string.installing_details, current, totalCount))
                .setContentText(context.getString(R.string.installing, ""))
                .setSmallIcon(R.mipmap.ic_launcher);
        mNotifyManager.notify(14099, mBuilder.build());
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        installApps();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        mBuilder.setContentTitle(context.getString(R.string.installed_details, current))
                .setContentText(context.getString(R.string.installation_finished))
                .setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setProgress(totalCount, current, false);
        mNotifyManager.notify(14099, mBuilder.build());

        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(ApplicationBean... info) {
        Bitmap bitmap = null;
        //Drawable icon = info[0].icon;
        try {

            //bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight()
            //        , Bitmap.Config.ARGB_8888);
            //Canvas canvas = new Canvas(bitmap);
            //icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            //icon.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBuilder.setProgress(totalCount, current, false);
        mBuilder.setContentTitle(context.getString(R.string.installing_details, current, totalCount))
                .setContentText(context.getString(R.string.installing, info[0].name));
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mNotifyManager.notify(14099, mBuilder.build());
        super.onProgressUpdate(info);

    }

    public void installApps() {

        for(ApplicationBean packageInfo: packages){
            current++;
            publishProgress(new ApplicationBean[]{packageInfo});
            try {
                if (isRooted()) {
                    final String downloadTarget = AppDownloader.getAbsoluteFilenameOfDownloadTarget(context, packageInfo);
                    installApp(downloadTarget);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void installApp(String filename) {
//        Util.waitForAllDownloadsToFinish(context);
        //TODO use RootInstaller here
        File file = new File(filename);
        if(file.exists()){
            try {
                String command;
                command = "pm install -r " + filename;
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
                if(proc.exitValue() != 0) {
                    BufferedReader stdError = new BufferedReader(
                            new InputStreamReader(proc.getErrorStream()));
                    String s = null;
                    String error = "";
                    while ((s = stdError.readLine()) != null)
                    {
                        error = s + "\n";
                    }
                    final String finalError = error;
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, finalError, Toast.LENGTH_LONG).show();

                        }
                    });

                }
            } catch (final Exception e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            }
        }
    }

    public boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

}

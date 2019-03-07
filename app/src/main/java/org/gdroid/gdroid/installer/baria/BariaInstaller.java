/*
 * Copyright (C) 2019 Andreas Redmer <ar-gdroid@abga.be>
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

package org.gdroid.gdroid.installer.baria;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.gdroid.gdroid.AppDownloader;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.io.File;
import java.util.ArrayList;

public class BariaInstaller {

    private final Context context;

    public BariaInstaller(Context context)
    {
        this.context = context;
    }

//    AutoRootAppInstallTask autoRootAppInstallTask;
    public void orderApkInstallations(final ArrayList<ApplicationBean> apps) {

        if(apps != null){
            if (Util.isRooted()) {
                AlertDialog.Builder questionDialog = new AlertDialog.Builder(context);
                questionDialog.setTitle(R.string.confirmation)
                        .setMessage(R.string.use_root)
                        .setIcon(R.mipmap.ic_launcher);

                questionDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AutoRootAppInstallTask autoRootAppInstallTask = new AutoRootAppInstallTask(context, apps);
                        autoRootAppInstallTask.execute();
                    }
                });

                questionDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        installAppsManually(apps);
                    }
                });

                // bugfix #113 : if the activity is not running any more it can crash here
                try {
                    questionDialog.create().show();
                }catch (Throwable t)
                {
                    Log.w("BariaInstaller", "Activity not running any more. Not showing a dialog.");
                }
            }
            else
            {
                installAppsManually(apps);
            }
        }
    }

    private void installAppsManually(ArrayList<ApplicationBean> apps) {
        final ArrayList<String> apks = new ArrayList<>();
        for(int i=0;i<apps.size(); i++) {
            final String absFile = AppDownloader.getAbsoluteFilenameOfDownloadTarget(context, apps.get(i));
            apks.add(absFile);
        }

        // Util.waitForAllDownloadsToFinish(); still doesn't work. fetch2 sucks as well!!
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // wait for each file to be completely downloaded
                for (String fn:apks) {
                    File f = new File (fn);
                    Util.waitForFileToBeStable(f);
                }
                //then do the installations
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, ManualAppInstallActivity.class);
                        intent.putExtra("apps", apks);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }
        });
    }
}

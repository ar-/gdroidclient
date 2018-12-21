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
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Func;

import org.gdroid.gdroid.beans.ApplicationBean;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

class AppDownloader {

    public static final String repoBaseUrl = "https://f-droid.org/repo/";

    public static void download(final Context context, ApplicationBean mApp) {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(5)
                .setNamespace("AppDownloader")
                .build();

        Fetch fetch = Fetch.Impl.getInstance(fetchConfiguration);

        String url = repoBaseUrl+mApp.apkname;
        final String file = context.getExternalCacheDir()+"/"+mApp.apkname;


        final Request request = new Request(url, file);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        fetch.enqueue(request, new Func<Request>() {
            @Override
            public void call(@NotNull Request result) {
            //Request was successfully enqueued for download.

            }
        }, new Func<Error>() {
            @Override
            public void call(@NotNull Error result) {
                //An error occurred enqueuing the request.

            }
        });

        fetch.addListener(new FetchListener() {
            @Override
            public void onAdded(Download download) {

            }

            @Override
            public void onQueued(Download download, boolean b) {

            }

            @Override
            public void onWaitingNetwork(Download download) {

            }

            @Override
            public void onCompleted(Download download) {

                Log.e ("FLAG","done");
                File otaFile = new File(file);

                Uri uri = Uri.fromFile(otaFile);
                final Intent intent = new Intent();
                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT < 14) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                } else if (Build.VERSION.SDK_INT < 16) {
                    intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(uri);
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
                } else if (Build.VERSION.SDK_INT < 24) {
                    intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(uri);
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                } else {
                    intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                }
                //startActivityForResult(intent, 0);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //startActivityForResult(intent, 0);
                        context.startActivity(intent);

                        // own parcelable cursor:
                        // https://stackoverflow.com/questions/17527095/could-not-write-cursorwindow-to-parcel-due-to-error-2147483641/19976499

                        // must use another download lib to prevent this
//                                11-07 06:24:14.995 15992-14156/? E/CursorWindow: Could not allocate
//                                CursorWindow
//                                '/data/data/com.android.providers.downloads/databases/downloads.db' of size 2097152 due to error -12.
//                                11-07 06:24:15.005 15992-14156/? E/DownloadManager: [11771] Failed: android.database.CursorWindowAllocationException: Cursor window allocation of 2048 kb failed. # Open Cursors=723 (# cursors opened by pid 13969=723)
                    }
                });

            }

            @Override
            public void onError(Download download, Error error, Throwable throwable) {

            }

            @Override
            public void onDownloadBlockUpdated(Download download, DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(Download download, List<? extends DownloadBlock> list, int i) {

            }

            @Override
            public void onProgress(Download download, long l, long l1) {

            }

            @Override
            public void onPaused(Download download) {

            }

            @Override
            public void onResumed(Download download) {

            }

            @Override
            public void onCancelled(Download download) {

            }

            @Override
            public void onRemoved(Download download) {

            }

            @Override
            public void onDeleted(Download download) {

            }
        });

    }
}

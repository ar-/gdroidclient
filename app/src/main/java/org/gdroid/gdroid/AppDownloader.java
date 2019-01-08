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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.EnqueueAction;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Func;
import com.tonyodev.fetch2core.MutableExtras;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.installer.Installer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.OkHttpClient;

public class AppDownloader {

    public static final String repoBaseUrl = "https://f-droid.org/repo/";

    public static Request download(final Context context, ApplicationBean mApp, final boolean install) {
        final Fetch fetch = getFetch(context);
        final Installer installer = Util.getAppInstaller(context);

        String url = repoBaseUrl+mApp.apkname;
        final String file = getAbsoluteFilenameOfDownloadTarget(context, mApp);


        final MutableExtras extras = new MutableExtras();
        extras.putString("id", mApp.id);

        final Request request = new Request(url, file);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        request.setExtras(extras);

        request.setEnqueueAction(EnqueueAction.REPLACE_EXISTING); // can be removed when this is fixed https://github.com/tonyofrancis/Fetch/issues/295
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
                fetch.removeListener(this);

                Runnable onComplete = new Runnable() {
                    @Override
                    public void run() {
                        if (context instanceof AppDetailActivity) {
                            final AppDetailActivity ada = (AppDetailActivity) context;
                            ada.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ada.updateInstallStatus(Status.NONE);
                                    } catch (Throwable t) {
                                        Log.e("ADA", "error in updateInstallStatus", t);
                                    }
                                }
                            });
                        }
                    }
                };

                Log.d("ADL", "done");
                String appId = download.getExtras().getString("id","");
                AppDatabase db = AppDatabase.get(context);
                ApplicationBean ab = db.appDao().getApplicationBean(appId);
                db.close();
                final String fn = getAbsoluteFilenameOfDownloadTarget(context, ab);
                if (install)
                    installer.installApp(context, file, onComplete);
            }

            @Override
            public void onError(Download download, Error error, Throwable throwable) {

            }

            @Override
            public void onDownloadBlockUpdated(Download download, @NotNull DownloadBlock downloadBlock, int totalBlocks) {

            }

            @Override
            public void onStarted(Download download, @NotNull List<? extends DownloadBlock> downloadBlocks, int totalBlocks) {

            }

            @Override
            public void onProgress(Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {

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

        return request;
    }

    @NonNull
    public static String getAbsoluteFilenameOfDownloadTarget(Context context, ApplicationBean mApp) {
        return context.getExternalCacheDir()+"/"+mApp.apkname;
    }

    static Fetch fetch = null;
    @NonNull
    public static Fetch getFetch(Context context) {
        if (fetch !=null && ! fetch.isClosed() )
            return fetch;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(5)
                .setNamespace("AppDownloader")
                .setHttpDownloader(new OkHttpDownloader(okHttpClient))
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        return fetch;
    }

}

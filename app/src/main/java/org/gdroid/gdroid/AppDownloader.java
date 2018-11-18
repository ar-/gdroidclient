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

import android.content.Context;

import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.Func;

import org.gdroid.gdroid.beans.ApplicationBean;
import org.jetbrains.annotations.NotNull;

class AppDownloader {

    public static final String repoBaseUrl = "https://f-droid.org/repo/";

    public static void download(Context context, ApplicationBean mApp) {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(5)
                .setNamespace("AppDownloader")
                .build();

        Fetch fetch = Fetch.Impl.getInstance(fetchConfiguration);

        String url = repoBaseUrl+mApp.apkname;
        String file = context.getExternalCacheDir()+"/"+mApp.apkname;


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

    }
}

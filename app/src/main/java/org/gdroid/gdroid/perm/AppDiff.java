/*
**
** Copyright 2018, Andreas Redmer <ar-gdroid@abga.be>
** Copyright 2007, The Android Open Source Project
** Copyright 2015 Dominik Schürmann <dominik@dominikschuermann.de>
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package org.gdroid.gdroid.perm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.gdroid.gdroid.beans.ApplicationBean;

/**
 * Represents the permissions difference between the installed APK, and the
 * update APK represented by {@link ApplicationBean}.
 */
public class AppDiff {

    public final PackageInfo apkPackageInfo;
    public final ApplicationInfo installedApplicationInfo;

    public AppDiff(Context context, ApplicationBean apk) {
        PackageManager pm = context.getPackageManager();
        apkPackageInfo = new PackageInfo();
        apkPackageInfo.packageName = apk.id;
        apkPackageInfo.applicationInfo = new ApplicationInfo();
        final String permissions = apk.permissions;
        if (!TextUtils.isEmpty(permissions))
            apkPackageInfo.requestedPermissions = permissions.split(",");
        else
            apkPackageInfo.requestedPermissions = new String[0];

        String packageName = apkPackageInfo.packageName;
        // Check if there is already a package on the device with this name
        // but it has been renamed to something else.
        final String[] oldName = pm.canonicalToCurrentPackageNames(new String[]{packageName});
        if (oldName != null && oldName.length > 0 && oldName[0] != null) {
            packageName = oldName[0];
            apkPackageInfo.packageName = packageName;
            apkPackageInfo.applicationInfo.packageName = packageName;
        }
        // Check if package is already installed
        ApplicationInfo applicationInfo;
        try {
            // This is a little convoluted because we want to get all uninstalled
            // apps, but this may include apps with just data, and if it is just
            // data we still want to count it as "installed".
            //noinspection WrongConstant (lint is actually wrong here!)
            applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED) == 0) {
                applicationInfo = null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        installedApplicationInfo = applicationInfo;
    }
}

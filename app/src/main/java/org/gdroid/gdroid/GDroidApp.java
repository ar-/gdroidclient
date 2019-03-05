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

package org.gdroid.gdroid;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.gdroid.gdroid.pref.Pref;

import java.util.List;

@ReportsCrashes(mailTo = "incoming+gdroid-gdroidclient-9250542-issue-@incoming.gitlab.com",
        mode = ReportingInteractionMode.DIALOG,
        reportDialogClass = org.gdroid.gdroid.acra.CrashReportActivity.class,
        reportSenderFactoryClasses = org.gdroid.gdroid.acra.CrashReportSenderFactory.class,
        customReportContent = {
                ReportField.USER_COMMENT,
                ReportField.PACKAGE_NAME,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PRODUCT,
                ReportField.BRAND,
                ReportField.PHONE_MODEL,
                ReportField.DISPLAY,
                ReportField.TOTAL_MEM_SIZE,
                ReportField.AVAILABLE_MEM_SIZE,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE_HASH,
                ReportField.STACK_TRACE,
        }
)
public class GDroidApp extends Application {
    private static GDroidApp instance;
    private static final String TAG = "GDroidApp";
    private static final String ACRA_ID = BuildConfig.APPLICATION_ID + ":acra";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Pref.setup(this);
        Pref preferences = Pref.get();

        if (preferences.sendCrashReports()) {
            ACRA.init(this);
            if (isAcraProcess()) {
                return;
            }
        }

//        UpdateService.schedule(getApplicationContext());

    }

    /**
     * Asks if the current process is "org.gdroid.gdroid:acra".
     * <p>
     * This is helpful for bailing out of the {@link GDroidApp#onCreate} method early, preventing
     * problems that arise from executing the code twice.
     * Note that it is not perfect, because some devices seem to not provide a list of running app
     * processes when asked. In such situations, G-Droid may regress to the behaviour where some
     * services may run twice and thus cause weirdness or slowness. However that is probably better
     * for end users than experiencing a deterministic crash every time G-Droid is started.
     */
    private boolean isAcraProcess() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
        if (processes == null) {
            return false;
        }

        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.pid == pid && ACRA_ID.equals(processInfo.processName)) {
                return true;
            }
        }

        return false;
    }


}

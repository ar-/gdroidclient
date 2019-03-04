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
package org.gdroid.gdroid.acra;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import org.acra.ReportField;
import org.acra.collections.ImmutableSet;
import org.acra.collector.CrashReportData;
import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
public class CrashReportSender implements ReportSender {

    private final ACRAConfiguration config;

    public CrashReportSender(ACRAConfiguration config) {
        this.config = config;
    }

    public void send(@NonNull Context context, @NonNull CrashReportData errorContent) {
        Intent emailIntent = new Intent("android.intent.action.SENDTO");
        emailIntent.setData(Uri.fromParts("mailto", this.config.mailTo(), null));
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String[] subjectBody = this.buildSubjectBody(context, errorContent);
        emailIntent.putExtra("android.intent.extra.SUBJECT", subjectBody[0]);
        emailIntent.putExtra("android.intent.extra.TEXT", subjectBody[1]);
        context.startActivity(emailIntent);
    }

    private String[] buildSubjectBody(Context context, CrashReportData errorContent) {
        ImmutableSet<ReportField> fields = this.config.getReportFields();
        if (fields.isEmpty()) {
            return new String[]{"No ACRA Report Fields found."};
        }

        String subject = context.getPackageName() + " Crash Report";
        StringBuilder builder = new StringBuilder();
        for (ReportField field : fields) {
            builder.append(field.toString()).append('=');
            builder.append(errorContent.get(field));
            builder.append('\n');
            if ("STACK_TRACE".equals(field.toString())) {
                String stackTrace = errorContent.get(field);
                if (stackTrace != null) {
                    subject = context.getPackageName() + ": "
                            + stackTrace.substring(0, stackTrace.indexOf('\n'));
                    if (subject.length() > 72) {
                        subject = subject.substring(0, 72);
                    }
                }
            }
        }

        return new String[]{subject, builder.toString()};
    }
}

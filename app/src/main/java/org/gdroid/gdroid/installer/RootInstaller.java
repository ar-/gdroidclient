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

package org.gdroid.gdroid.installer;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class RootInstaller implements Installer {

    @Override
    public void installApp(final Context context, String filename, Runnable postInstall) {

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

        if (postInstall != null)
        {
            postInstall.run();
        }
    }
}

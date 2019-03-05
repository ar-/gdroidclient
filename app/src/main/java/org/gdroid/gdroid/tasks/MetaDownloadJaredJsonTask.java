/*
 * Copyright (C) 2018-2019 Andreas Redmer <ar-gdroid@abga.be>
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

package org.gdroid.gdroid.tasks;

import android.app.Activity;
import android.widget.Toast;

import org.gdroid.gdroid.MainActivity;
import org.gdroid.gdroid.R;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.pref.Pref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MetaDownloadJaredJsonTask extends DownloadJaredJsonTask {
    List<ApplicationBean> mAppBeanList;
    public MetaDownloadJaredJsonTask(MainActivity mainActivity, String jsonFileInJar, List<ApplicationBean> abl) {
        super(mainActivity, null, jsonFileInJar);
        mAppBeanList = abl;
    }

    @Override
    protected List<ApplicationBean> doInBackground(String... urls) {
        List<ApplicationBean> abl = new ArrayList<>();
        try {
            final MetaBeanJsonParser parser = new MetaBeanJsonParser(mAppBeanList);
            parser.initMetricWeightsFromPreferences(mContext);
            abl = loadJsonFromNetwork(urls[0], mJsonFileInJar, parser);

            // arriving here without error means a complete update has been successfully done
            Pref.get().setLastUpdateCheck(System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, R.string.could_not_download_gdroid_metadata,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }

        return abl;
    }

    @Override
    protected void onPostExecute(List<ApplicationBean> result) {
        //super.onPostExecute(result);
        // nothing
    }
}

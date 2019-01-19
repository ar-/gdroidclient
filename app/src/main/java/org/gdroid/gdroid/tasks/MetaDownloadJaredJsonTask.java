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

package org.gdroid.gdroid.tasks;

import org.gdroid.gdroid.MainActivity;
import org.gdroid.gdroid.beans.ApplicationBean;

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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return abl;
    }

    @Override
    protected void onPostExecute(List<ApplicationBean> result) {
        //super.onPostExecute(result);
        // nothing
    }
}

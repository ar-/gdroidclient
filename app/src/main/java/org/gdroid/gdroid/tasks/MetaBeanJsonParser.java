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

import android.util.Log;

import org.gdroid.gdroid.MetaMetric;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class MetaBeanJsonParser implements JsonParser {
    // map: pkgname -> AppBean
    Map<String,ApplicationBean> abMap;

    public MetaBeanJsonParser(List<ApplicationBean> appBeanList) {
        abMap = new HashMap<>(appBeanList.size());
        for (ApplicationBean ab :
                appBeanList) {
            abMap.put(ab.id,ab);
        }
    }

    @Override
    public List<ApplicationBean> getApplicationBeansFromJson(String jsonString) {
        List<ApplicationBean> entries = null;
        try {
            JSONObject root=new JSONObject(jsonString);
            Iterator<String> keys = root.keys();

            while(keys.hasNext()) {
                String appId = keys.next();
                final JSONObject content = root.getJSONObject(appId);
                jSonObjToAppBean(appId,content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entries;

    }

    private void jSonObjToAppBean(String appId, JSONObject content) throws JSONException {
        //annotate the existing list of the previous download
        ApplicationBean ab = abMap.get(appId);
        final JSONObject metrics = content.optJSONObject("metrics");
        //final JSONObject neighbours = content.optJSONObject("neighbours"); // TODO use neighbours

        if (metrics != null)
        {
            final double norm_spd = metrics.optDouble("m_github_stars_per_day_normalised");
            final double norm_a24 = metrics.getDouble("age_last_v_24");
            MetaMetric mm = new MetaMetric();
            if (! Double.isNaN(norm_spd))
            {
                // TODO let the user choose the weights
                mm.addMetric(norm_spd,1);
            }
            if (! Double.isNaN(norm_a24))
            {
                //TODO ask the user in the settings about the weight of each metric
                mm.addMetric(norm_a24,1);
            }
            ab.stars = (float) mm.getScaledValue(5.0);
        }

    }

}

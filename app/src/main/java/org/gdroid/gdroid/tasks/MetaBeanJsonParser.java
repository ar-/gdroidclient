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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.gdroid.gdroid.MetaMetric;
import org.gdroid.gdroid.Util;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class MetaBeanJsonParser extends AbstractJsonParser implements JsonParser{
    // map: pkgname -> AppBean
    Map<String,ApplicationBean> abMap;
    private int weight_upstream_ratings;
    private int weight_last_update_2_years;
    private int weight_release_cycle_time;

    public MetaBeanJsonParser(List<ApplicationBean> appBeanList) {
        abMap = new HashMap<>(appBeanList.size());
        for (ApplicationBean ab :
                appBeanList) {
            abMap.put(ab.id,ab);
        }
        weight_upstream_ratings = 1;
        weight_last_update_2_years = 1;
        weight_release_cycle_time = 1;
    }

    public void initMetricWeightsFromPreferences(Context context)
    {
        // read the weights of the metrics
        weight_upstream_ratings = Util.getWeightOfMetric(context,"weight_upstream_ratings");
        weight_last_update_2_years = Util.getWeightOfMetric(context,"weight_last_update_2_years");
        weight_release_cycle_time = Util.getWeightOfMetric(context,"weight_release_cycle_time");

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
        if (TextUtils.isEmpty(appId))
            return;
        //annotate the existing list of the previous download
        ApplicationBean ab = abMap.get(appId);

        // fix for #40 , app might not be there if F-Droid moved it to the archive
        if (ab == null)
            return;

        final JSONObject metrics = content.optJSONObject("metrics");
        //final JSONObject neighbours = content.optJSONObject("neighbours"); // TODO use neighbours

        if (metrics != null)
        {
            // store raw metrics for later use
            ab.metricsJson = metrics.toString();

            // eval the meta-metric

            final double norm_ghspd = metrics.optDouble("m_github_stars_per_day_normalised");
            final double norm_glspd = metrics.optDouble("m_gitlab_stars_per_day_normalised");
            final double norm_a24 = metrics.optDouble("age_last_v_24");
            final double norm_udf = metrics.optDouble("avg_update_frequency_normalised");
            MetaMetric mm = new MetaMetric();
            if (! Double.isNaN(norm_ghspd))
            {
                mm.addMetric(norm_ghspd,weight_upstream_ratings);
            }
            if (! Double.isNaN(norm_glspd))
            {
                mm.addMetric(norm_glspd,weight_upstream_ratings);
            }
            if (! Double.isNaN(norm_a24))
            {
                mm.addMetric(norm_a24,weight_last_update_2_years);
            }
            if (! Double.isNaN(norm_udf))
            {
                mm.addMetric(norm_udf,weight_release_cycle_time);
            }
            ab.stars = (float) mm.getScaledValue(5.0);
            ab.metriccount = mm.countMetrics();
        }

        // tags
        final JSONArray tags = content.optJSONArray("tags");
        if (tags != null)
        {
            ab.tags = new ArrayList<>();
            for (int i=0;i<tags.length();i++)
            {
                ab.tags.add(tags.get(i).toString());
            }
        }

        // screenshots array
        final Pair<JSONArray, String> phoneScreenshots = getLocalizedArrayItemAndLocale(content, "phoneScreenshots");
        if (phoneScreenshots != null)
        {
            final String ssLocale = phoneScreenshots.second;
            final JSONArray ssArray = phoneScreenshots.first;
            if (ssArray != null && ssLocale != null) {
                List<String> ssList = new ArrayList<>();
                for (int i = 0; i < ssArray.length(); i++) {
                    ssList.add(ssArray.get(i).toString());
                }
                // overwrite any existing entries, because the F-Droid ones are likely to be wrong, outdated, wrong language etc.
                ab.screenshots = TextUtils.join(";",ssList);
            }
        }



    }
}

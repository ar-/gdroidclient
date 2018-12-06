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

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

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
import java.util.Set;

class AppBeanJsonParser implements JsonParser{
    @Override
    public List<ApplicationBean> getApplicationBeansFromJson(String jsonString) {
        List<ApplicationBean> entries = null;
        try {
            JSONObject jo=new JSONObject(jsonString);
            final JSONArray apps = jo.getJSONArray("apps");
            final JSONObject packages = jo.getJSONObject("packages");
            entries = new ArrayList<>(apps.length());
            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.getJSONObject(i);
                try {
                    ApplicationBean ab = jSonObjToAppBean(app, packages);
                    if (!TextUtils.isEmpty(ab.id))
                        entries.add(ab);
                } catch (JSONException e)
                {
                    Log.e("Parser","ignoring "+app.optString("name", "unknown app"));
                    e.printStackTrace();
                    //skip
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entries;
    }


    private ApplicationBean jSonObjToAppBean(JSONObject app, JSONObject packages) throws JSONException {
        ApplicationBean ab = new ApplicationBean();
        ab.id = app.getString("packageName");
        ab.added = app.getLong("added");
        ab.lastupdated = app.getLong("lastUpdated");
        ab.license = app.getString("license");
        ab.icon = app.optString("icon");

        ab.name = getLocalizedStringItem(app, "name");
        ab.summary = getLocalizedStringItem(app, "summary"); // summary is optional
        ab.desc = getLocalizedStringItem(app, "description");
        ab.whatsNew = getLocalizedStringItem(app, "whatsNew");

        // featureGraphic localized and optional string
        final Pair<String, String> fg = getLocalizedStringItemAndLocale(app, "featureGraphic");
        if (fg != null)
            ab.featureGraphic = fg.second + "/" + fg.first;

        //get categories as string separated list
        ab.categories = new ArrayList<>();
        final JSONArray cats = app.getJSONArray("categories");
        for (int i=0;i<cats.length();i++)
        {
            ab.categories.add(cats.get(i).toString());
        }

        ab.web = app.optString("webSite");
        ab.source = app.optString("sourceCode");
        ab.tracker = app.optString("issueTracker");
        ab.changelog = app.optString("changelog");
        ab.bitcoin = app.optString("bitcoin");
        ab.liberapay = app.optString("liberapayID");
        ab.marketversion = app.optString("suggestedVersionName");
        ab.marketvercode = app.optString("suggestedVersionCode");
        ab.author = app.optString("authorName");
        ab.email = app.optString("authorEmail");

        // apk name from packages
        // package must be found, otherwise app ise useles,as it wont hav an APK
        final JSONArray appPackages = packages.getJSONArray(ab.id);
        final JSONObject latestPackage = appPackages.getJSONObject(0);
        ab.apkname = latestPackage.getString("apkName");

        // marketversion and marketvercode are sometimes wrong in the json, like a higher version that doesn't exist yet
        // or just an empty string. let's redefine it, and use the lates version as merket version.
        ab.marketversion = latestPackage.getString("versionName");
        ab.marketvercode = latestPackage.getString("versionCode");


        // permissions from packages (optional)
        final JSONArray permissionsArray = latestPackage.optJSONArray("uses-permission");
        if (permissionsArray != null)
        {
            List<String> pList = new ArrayList<>();
            for (int i = 0 ; i < permissionsArray.length() ; i++)
            {
                final String per = permissionsArray.getJSONArray(i).getString(0);
                pList.add(per);
            }
            ab.permissions = TextUtils.join(",",pList);
        }

        // antifeatures
        final JSONArray afArray = app.optJSONArray("antiFeatures");
        if (afArray != null) {
            List<String> afList = new ArrayList<>();
            for (int i = 0; i < afArray.length(); i++) {
                afList.add(afArray.get(i).toString());
            }
            ab.antifeatures = TextUtils.join(",",afList);
        }

        // screenshots array
        final Pair<JSONArray, String> phoneScreenshots = getLocalizedArrayItemAndLocale(app, "phoneScreenshots");
        if (phoneScreenshots != null)
        {
            final String ssLocale = phoneScreenshots.second;
            final JSONArray ssArray = phoneScreenshots.first;
            if (ssArray != null && ssLocale != null) {
                List<String> ssList = new ArrayList<>();
                for (int i = 0; i < ssArray.length(); i++) {
                    ssList.add(ssLocale+"/phoneScreenshots/"+ssArray.get(i).toString());
                }
                ab.screenshots = TextUtils.join(";",ssList);
            }

        }


        return ab;
    }

    /**
     *
     * @param app
     * @param name
     * @return the string only (taken from the best locale)
     * @throws JSONException
     */
    private String getLocalizedStringItem(JSONObject app, String name) throws JSONException {
        final Pair<String, String> ret = getLocalizedStringItemAndLocale(app, name);
        if (ret == null)
            return null;
        return ret.first;
    }

    /**
     *
     * @param app
     * @param name
     * @return A pair of (Item,Locale)
     * @throws JSONException
     */
    private Pair<String,String> getLocalizedStringItemAndLocale(JSONObject app, String name) throws JSONException {
        final String repoLocale="en";

        // Map locale -> itemcontent
        Map<String, String> availalableLocales = new HashMap<>();

        //gather available Locales for this item
        final String unLocalisedItem = app.optString(name);
        if (!TextUtils.isEmpty(unLocalisedItem))
        {
            availalableLocales.put(repoLocale,unLocalisedItem);
        }

        final JSONObject localized = app.optJSONObject("localized");
        if (localized != null)
        {
            Iterator<String> keys = localized.keys();

            while(keys.hasNext()) {
                String resLocale = keys.next();
                final JSONObject localizedJSONObject = localized.getJSONObject(resLocale);
                final String localisedString = localizedJSONObject.optString(name);
                if (!TextUtils.isEmpty(localisedString))
                {
                    availalableLocales.put(resLocale,localisedString);
                }
            }
        }

        // for some items (like 'name') having none is bad, others (like 'whatsNew') are optional
        if (availalableLocales.isEmpty())
            return null;

        final Set<String> keySet = availalableLocales.keySet();
        String[] resLocales = keySet.toArray(new String[keySet.size()]);
        final String usableLocale = Util.getUsableLocale(resLocales);

        // this can't be null anymore, otherwise we wouldn't have arrived here
        return new Pair<String,String>(availalableLocales.get(usableLocale), usableLocale);
    }

    /**
     *
     * @param app
     * @param name
     * @return A pair of (ItemArray,Locale)
     * @throws JSONException
     */
    private Pair<JSONArray,String> getLocalizedArrayItemAndLocale(JSONObject app, String name) throws JSONException {
        final String repoLocale="en";

        // Map locale -> itemcontent
        Map<String, JSONArray> availalableLocales = new HashMap<>();

        //gather available Locales for this item
        final JSONArray unLocalisedItem = app.optJSONArray(name);
        if (unLocalisedItem != null)
        {
            availalableLocales.put(repoLocale,unLocalisedItem);
        }

        final JSONObject localized = app.optJSONObject("localized");
        if (localized != null)
        {
            Iterator<String> keys = localized.keys();

            while(keys.hasNext()) {
                String resLocale = keys.next();
                final JSONObject localizedJSONObject = localized.getJSONObject(resLocale);
                final JSONArray localisedArrayItem = localizedJSONObject.optJSONArray(name);
                if (localisedArrayItem != null)
                {
                    availalableLocales.put(resLocale,localisedArrayItem);
                }
            }
        }

        // for some items (like 'name') having none is bad, others (like 'whatsNew') are optional
        if (availalableLocales.isEmpty())
            return null;

        final Set<String> keySet = availalableLocales.keySet();
        String[] resLocales = keySet.toArray(new String[keySet.size()]);
        final String usableLocale = Util.getUsableLocale(resLocales);

        // this can't be null anymore, otherwise we wouldn't have arrived here
        return new Pair<JSONArray,String>(availalableLocales.get(usableLocale), usableLocale);
    }


}

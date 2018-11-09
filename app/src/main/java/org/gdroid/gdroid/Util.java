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

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.os.ConfigurationCompat;
import android.support.v4.os.LocaleListCompat;
import android.util.ArraySet;
import android.util.Log;

import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Util {
    public static Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    public static void setLastMenuItem (Context context, String menuItem)
    {
        String key = "lastmenuitem";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key,menuItem);
        editor.apply();
    }

    public static String getLastMenuItem (Context context)
    {
        String key = "lastmenuitem";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String setting = sharedPref.getString(key, "home");
        return setting;
    }

    public static void starApp (Context context, String appId)
    {
        String key = "starred";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> starred = sharedPref.getStringSet(key, new HashSet<String>());
        HashSet<String> wrtieCopy = new HashSet<>(starred);
        wrtieCopy.add(appId);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(key,wrtieCopy);
        editor.commit();
    }

    public static void unstarApp (Context context, String appId)
    {
        String key = "starred";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> starred = sharedPref.getStringSet(key, new HashSet<String>());
        HashSet<String> wrtieCopy = new HashSet<>(starred);
        wrtieCopy.remove(appId);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putStringSet(key,wrtieCopy);
        editor.commit();
    }

    public static boolean isAppstarred (Context context, String appId)
    {
        String key = "starred";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> starred = sharedPref.getStringSet(key, new HashSet<String>());
        return starred.contains(appId);
    }

    public static void toggleAppStarred (Context context, String appId) {
        if (isAppstarred(context,appId))
        {
            unstarApp(context,appId);
        }
        else
        {
            starApp(context,appId);
        }
    }

    public static List<ApplicationBean> getStarredApps(Context context)
    {
        String key = "starred";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> starred = sharedPref.getStringSet(key, new HashSet<String>());
        List<ApplicationBean> ret = new ArrayList<>();
        AppDatabase db = AppDatabase.get(context);
        for (String starredAppId: starred) {
            final ApplicationBean app = db.appDao().getApplicationBean(starredAppId);
            ret.add(app);
        }
        return ret;
    }

    public static List<ApplicationBean> getInstalledApps(Context context)
    {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationBean> ret = new ArrayList<>();
        AppDatabase db = AppDatabase.get(context);
        for (ApplicationInfo packageInfo : packages) {
            final ApplicationBean app = db.appDao().getApplicationBean(packageInfo.packageName);

            // only apps in the repo
            if (app!=null)
                ret.add(app);
        }
        return ret;
    }

    /**
     * uses the installed system locales as userLocales
     * Is similar to localeListCompat.getFirstMatch(resLocales) but returns only a definatetly available Locale.
     * @param resLocales
     * @return
     */
    public static String getUsableLocale(String[] resLocales) {
        //String[] systemLocales = Resources.getSystem().getAssets().getLocales();
//        List<Locale> systemLocales
        final LocaleListCompat localeListCompat = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        List<String> enabledLocalesAsStrings = new ArrayList<>();
        for (int i = 0 ; i< localeListCompat.size(); i++)
        {
            enabledLocalesAsStrings.add(localeListCompat.get(i).toString());
        }

////        final Locale[] systemLocales = Locale.get getAvailableLocales();
//        Set<String> localeSet = new HashSet<>();
//        for (Locale l :
//                systemLocales) {
//            l.toString();
//        }
//
        String[] userLocalesAsString = enabledLocalesAsStrings.toArray(new String[enabledLocalesAsStrings.size()]);
        if (userLocalesAsString.length==0)
        {
            Log.e("Util","no system locals detected, that must be an error. Putting 'en' in place to go ahead");
            userLocalesAsString = new String[]{"en"};
        }

        return  getUsableLocale( userLocalesAsString, resLocales);
    }
        /**
         * retruns one of the resLocals. the one that can be used to show to the user
         * @param userLocales ordered by priority
         * @param resLocales unordered
         * @return
         */
    public static String getUsableLocale(String[] userLocales, String[] resLocales){
        if (userLocales.length==0)
            throw new RuntimeException("userLocales cant be empty");
            //return ret;
        if (resLocales.length==0)
            throw new RuntimeException("resLocales cant be empty");
            //return ret;

        // a quick return just in case there is only one of each and they are equal
        if (userLocales[0].equals(resLocales[0]))
            return resLocales[0];

        String defaultResLocale = resLocales[0]; // chuck the first one in as default

        final List<String> resLocList = Arrays.asList(resLocales);

        for (String userLocale :
                userLocales) {
            // 1. check for exact match of locales
            if (resLocList.contains(userLocale))
                return userLocale;

            // 2. check plain language match
            final String userLang = getLangFromLocale(userLocale);
            if (resLocList.contains(userLang))
                return userLang;

            // 3. check if same language in other area-codes match (first hit is fine)
            for (String resLocale :
                    resLocales) {
                final String resLang = getLangFromLocale(resLocale);
                if (resLang.equals(userLang))
                    return resLocale;
            }

            // 4. repeat for all userLocales :-)
        }

        return defaultResLocale;
    }

    /**
     *
     * @param locale something like "de_AT" "de-DE" "de" ....
     * @return
     */
    private static String getLangFromLocale(String locale) {
        locale = locale.replace('_','-');
        if (locale.contains("-"))
        {
            final String[] parts = locale.split("-");
            // it can have more than 2 parts. format is:
            // language + "_" + country + "_" + (variant + "_#" | "#") + script + "-" + extensions
            return parts[0];
        }
        else
        {
            // there might be invalid names for manguages in upstream (obf,off,opf,opff)
            // but they will be irgores, because user can't have such a language activated
            return locale; // if there was no separator, it is probalby a language without location
        }
    }

}

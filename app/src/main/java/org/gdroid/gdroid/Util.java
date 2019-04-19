/*
 * Copyright (C) 2018,2019 Andreas Redmer <ar-gdroid@abga.be>
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

import android.annotation.TargetApi;
import android.app.Activity;
import android.arch.persistence.room.util.StringUtil;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.os.ConfigurationCompat;
import android.support.v4.os.LocaleListCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.gdroid.gdroid.beans.AppBeanNameComparator;
import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;
import org.gdroid.gdroid.beans.CommentBean;
import org.gdroid.gdroid.beans.OrderByCol;
import org.gdroid.gdroid.installer.DefaultInstaller;
import org.gdroid.gdroid.installer.Installer;
import org.gdroid.gdroid.installer.RootInstaller;
import org.gdroid.gdroid.pref.Pref;
import org.gdroid.gdroid.tasks.DownloadCommentsTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {


    public static final String NOTIFICATION_CHANEL_ID = "30009";
    public static final int NOTIFICATION_ID = 14099;
    public static final String TAG = "Util";
    public static final int UNINSTALL_FINISHED = 1;

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
        return sharedPref.getString(key, "home");
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
        //noinspection ConstantConditions
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
        db.close();
        Collections.sort(ret, new AppBeanNameComparator(context,
                Util.getOrderByColumn(context), Util.getOrderByDirection(context).equals("ASC")));
        return ret;
    }

    public static void hideApp (Context context, String appId)
    {
        AppDatabase db = AppDatabase.get(context);
        db.appDao().hideApp(appId, true);
        db.close();
    }

    public static void unhideApp (Context context, String appId)
    {
        AppDatabase db = AppDatabase.get(context);
        db.appDao().hideApp(appId, false);
        db.close();
    }

    public static List<ApplicationBean> getHiddenApps(Context context)
    {
        List<ApplicationBean> ret = new ArrayList<>();
        AppDatabase db = AppDatabase.get(context);
        final ApplicationBean[] hiddenApps = db.appDao().getAllHiddenApps();
        db.close();
        Collections.addAll(ret, hiddenApps);
        Collections.sort(ret, new AppBeanNameComparator(context,
                Util.getOrderByColumn(context), Util.getOrderByDirection(context).equals("ASC")));
        return ret;
    }

    public static int getWeightOfMetric(Context context, String metric)
    {
        try
        {
            return PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getInt(metric, 1);
        }
        catch (Throwable t)
        {
            return 1;
        }
    }

    public static List<ApplicationBean> getInstalledApps(Context context)
    {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> packageNames = new ArrayList<>(packages.size());
        for (ApplicationInfo packageInfo : packages) {
            packageNames.add(packageInfo.packageName);
        }

        // bugfix #115: if someone has more than 999 apps installed: prevent crash here
        if (packageNames.size()>989)
        {
            List<String> choppedList = new ArrayList<>(packageNames.subList(0, 990));
            packageNames = choppedList;
        }

        AppDatabase db = AppDatabase.get(context);
        List<ApplicationBean> ret = db.appDao().getSomeApplicationBeansList(packageNames);
        db.close();
        Collections.sort(ret, new AppBeanNameComparator(context,
                Util.getOrderByColumn(context),
                Util.getOrderByDirection(context).equals("ASC"),
                true));
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

    public static String getStringResourceByName(Context c, String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "string", packageName);
        if (resId == 0)
            return aString;
        return c.getString(resId);
    }

    public static String getLocalisedAntifeatureDescription(Context c, String af)
    {
        // found in json: "Ads", "NonFreeAdd", "NonFreeAssets", "NonFreeDep", "NonFreeNet", "NoSourceSince", "Tracking", "UpstreamNonFree"
        // in the F-Droid string XML all antifeatures are prefixed by 'anti', most of them suffixed by 'list' and all lowercase
        // and then sometimes again renamed, that is way to inconsistent, so I rename them with a proper schema using the
        // names in the upstream json file

        return getStringResourceByName(c, "anti_"+af.toLowerCase());
    }

    public static String getLocalisedCategoryName(Context c, String cn)
    {
        cn = cn.replace("& ","").replace(" ","_");
        return getStringResourceByName(c, "category_"+cn);
    }

    public static String getLocalisedOrderByColumn(Context c, OrderByCol obc)
    {
        switch (obc)
        {
            case added:
                return c.getString(R.string.added_on).replace("%s","");
            case lastupdated:
                return c.getString(R.string.last_update);
            case stars:
                return c.getString(R.string.stars);
            case name:
                return c.getString(R.string.name);
        }
        return getStringResourceByName(c, "unset_col");
    }

    /**
     * references to all string IDs so they don't get removed by the "remove unused" function
     */
    public static void getReferenceToallStrings()
    {
        int i = 0;

        // anti
        i = R.string.antifeatures;
        i = R.string.anti_ads;
        i = R.string.anti_nonfreeadd;
        i = R.string.anti_nonfreeassets;
        i = R.string.anti_nonfreedep;
        i = R.string.anti_nonfreenet;
        i = R.string.anti_nosourcesince;
        i = R.string.anti_tracking;
        i = R.string.anti_upstreamnonfree;

        // in archive
        i = R.string.anti_disabledalgorithm;
        i = R.string.anti_knownvuln;

        // cats
        i = R.string.category_Development;
        i = R.string.category_Games;
        i = R.string.category_Graphics;
        i = R.string.category_Internet;
        i = R.string.category_Money;
        i = R.string.category_Multimedia;
        i = R.string.category_Navigation;
        i = R.string.category_Phone_SMS;
        i = R.string.category_Reading;
        i = R.string.category_Science_Education;
        i = R.string.category_Security;
        i = R.string.category_Sports_Health;
        i = R.string.category_System;
        i = R.string.category_Theming;
        i = R.string.category_Time;
        i = R.string.category_Writing;

        // tags
        i = R.string.calendar;
        i = R.string.camera;
        i = R.string.email_client;
        i = R.string.file_browser;
        i = R.string.gallery;
        i = R.string.music_player;
        i = R.string.web_browser;
        i = R.string.youtube_player;

    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static String getInstalledVersionOfApp(Context context, String packageName) {
        try {
            PackageInfo pinfo = null;
            pinfo = context.getPackageManager().getPackageInfo(packageName, 0);
            String verName = pinfo.versionName;
            return verName;
        }
        catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static int getInstalledVersionCodeOfApp(Context context, String packageName) {
        try {
            PackageInfo pinfo = null;
            pinfo = context.getPackageManager().getPackageInfo(packageName, 0);
            int code = pinfo.versionCode;
            return code;
        }
        catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static boolean isAppUpdateable (Context context, ApplicationBean applicationBean)
    {
        final long installedVersionCodeOfApp = Util.getInstalledVersionCodeOfApp(context, applicationBean.id);
        if (!TextUtils.isEmpty(applicationBean.marketvercode) && installedVersionCodeOfApp > 0 )
        {
            final int marketvercode = Integer.parseInt(applicationBean.marketvercode);
            return marketvercode > installedVersionCodeOfApp;
        }
        return false;
    }

    private static boolean isFileExisting(String filename){

        File f1 = new File(filename);
        return f1.exists();
    }

    private static boolean deleteFile( String filename){
        File folder1 = new File(filename);
        return folder1.delete();
    }

    public static void deleteFileIfExist(String filename){
        if (isFileExisting(filename))
        {
            deleteFile(filename);
        }
    }

    public static boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

    public static Installer getAppInstaller(Context context) {
        boolean isRooted = isRooted();
        boolean useRoot = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_root",false);
        if (isRooted && useRoot)
        {
            return new RootInstaller();
        }
        return new DefaultInstaller();
    }

    public static boolean isListViewPreferred(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_list_view",false);
    }

    public static String getOrderByColumnPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("order_by_column","lastupdated DESC");
    }

    public static String getOrderByColumnAsSring(Context context) {
        return getOrderByColumnPreference(context).split(" ")[0];
    }

    public static OrderByCol getOrderByColumn(Context context) {
        return Enum.valueOf(OrderByCol.class, getOrderByColumnAsSring(context));
    }

    public static String getOrderByDirection(Context context) {
        return getOrderByColumnPreference(context).split(" ")[1];
    }

    public static void setOrderByColumnPreference(Context context, String orderByColumn)
    {
        String key = "order_by_column";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, orderByColumn);
        editor.apply();
    }

    public static void waitForAllDownloadsToFinish(final Context context) {
        AppDownloader.getFetch(context).awaitFinishOrTimeout(120000L);
    }

    public static void waitForFileToBeStable(File file) {
        long fileSize = file.length();
        while (true)
        {
            Log.d(TAG, "waiting for download "+file.getName()+" to settle. Got now "+fileSize+" bytes");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long newFileSize = file.length();
            if (newFileSize == 0L)
                continue;
            if (newFileSize == fileSize)
                break;
            fileSize = newFileSize;
        }
    }

    public static String convertPackageNameToHashtag(String pkgName)
    {
        return pkgName.replace('.', '_');
    }

    public static String convertHashtagToPackageName(String hashtag)
    {
        return hashtag.replace('_', '.');
    }


    /**
     * The most preferred ABI is the first element in the list.
     */
    @TargetApi(21)
    @SuppressWarnings("deprecation")
    public static String[] getAbis() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Build.SUPPORTED_ABIS;
        }
        return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
    }

    private static List<ApplicationBean> cachedRecentlyCommentedApps = new ArrayList<>();
    public static List<ApplicationBean> getRecentlyCommentedApps(Context mContext, int mLimit) {

        if (isUIThread() && !cachedRecentlyCommentedApps.isEmpty())
        {
            return cachedRecentlyCommentedApps;
        }

        List<ApplicationBean> ret = new ArrayList<>(40);
        List<String> appIds = new ArrayList<>(40*2);
        List<CommentBean> comments = new ArrayList<>(40);
        final DownloadCommentsTask downloadCommentsTask = new DownloadCommentsTask(comments, new Runnable() {
            @Override
            public void run() {
            }
        });

        try {
            // fetch
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            comments = downloadCommentsTask.doInBackground("https://mastodon.technology/api/v1/timelines/tag/" + Const.HASHTAG_APP_COMMENTS + "?limit=40");

            // extract app IDs
            for (CommentBean cb : comments) {
                Pattern pattern = Pattern.compile("#<span>\\w+</span>", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(cb.content);
                while (matcher.find()) {
                    String possibleHashtag = matcher.group();
                    possibleHashtag = possibleHashtag.replace("#<span>", "");
                    possibleHashtag = possibleHashtag.replace("</span>", "");
                    if (possibleHashtag.equals(Const.HASHTAG_APP_COMMENTS))
                        continue;

                    if (!appIds.contains(possibleHashtag))
                        appIds.add(possibleHashtag);

                    // arrived here we have a match, most likely an app id
                }
            }

            // store each successful fetch in Pref
            if (!appIds.isEmpty()) {
                Pref.get().setLastCommentedApps(TextUtils.join(";", appIds));
            }
        }
        catch (Throwable t)
        {
            // fallback to the cached list from Pref below
        }

        if (appIds.isEmpty())
        {
            final String[] idArray = Pref.get().getLastCommentedApps().split(";");
            appIds = new ArrayList<>(Arrays.asList(idArray));
        }

        // we have to get the apps one by one from the DB to retain the order
        for (String appId : appIds)
        {
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean ab = db.appDao().getApplicationBean(convertHashtagToPackageName(appId));
            if (ab != null)
            {
                ret.add(ab);
            }
        }

        cachedRecentlyCommentedApps = ret;
        int limit = Math.min(ret.size()-1,mLimit);
        limit = Math.max(0,limit);
        return new ArrayList(ret.subList(0, limit));
    }

    public static boolean isUIThread()
    {
        return Thread.currentThread().equals( Looper.getMainLooper().getThread() );
    }
}

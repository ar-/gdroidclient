package org.gdroid.gdroid;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.gdroid.gdroid.beans.AppDatabase;
import org.gdroid.gdroid.beans.ApplicationBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

}

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

package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.gdroid.gdroid.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * wraps a name of a collection and a list of apps to be contained in the collection, to be shown in the UI
 */
public class AppCollectionDescriptor {
    private final int mLimit;
    private final int mOffset;
    private Context mContext;
    private String name;
    private List<ApplicationBean> applicationBeanList;

    public AppCollectionDescriptor(Context context, String name) {
        this(context,name, 12);
    }

    public AppCollectionDescriptor(Context context, String name, int limit) {
        this(context,name, limit, 0);
    }

    public AppCollectionDescriptor(Context context, String name, int limit, int offset) {
        this.mContext = context;
        this.mOffset = offset;
        this.mLimit = limit;
        applicationBeanList = new ArrayList<>();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateAppsInCollection();
    }

    public void updateAppsInCollection() {
        String collectionName = name;
        if (collectionName.equals("Newest apps"))
        {
            AppDatabase db = AppDatabase.get(mContext);

            ApplicationBean[] appsInDb = db.appDao().getLastAdded(mLimit,mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.equals("Recently updated"))
        {
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getLastUpdated(mLimit,mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.equals("High rated"))
        {
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getHighRated(mLimit,mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.equals("Random apps"))
        {
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getRandom(mLimit,mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.equals("starred"))
        {
            applicationBeanList.clear();
            applicationBeanList.addAll(Util.getStarredApps(mContext));
        }
        else if (collectionName.equals("myapps"))
        {
            applicationBeanList.clear();
            applicationBeanList.addAll(Util.getInstalledApps(mContext));
        }
        else if (collectionName.equals("hiddenapps"))
        {
            applicationBeanList.clear();
            applicationBeanList.addAll(Util.getHiddenApps(mContext));
        }
        else if (collectionName.startsWith("search:"))
        {
            String searchString = collectionName.replace("search:","");
            searchString = "%"+searchString+"%";
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getAllAppsForSearchString(searchString, mLimit, mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.startsWith("search2:")) // level 2 search
        {
            String searchString = collectionName.replace("search2:","");
            searchString = "%"+searchString+"%";
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getAllAppsForSearch2String(searchString, mLimit, mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.startsWith("search3:")) // level 3 search
        {
            String searchString = collectionName.replace("search3:","");
            searchString = "%"+searchString+"%";
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getAllAppsForSearch3String(searchString, mLimit, mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.startsWith("author:"))
        {
            String au = collectionName.replace("author:","");
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getAppsByAuthor(au, mLimit, mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.startsWith("cat:"))
        {
            String cat = collectionName.replace("cat:","");
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getAllAppsForCategory(cat, mLimit, mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
        else if (collectionName.startsWith("tag:"))
        {
            String tag = collectionName.replace("tag:","");
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getAllAppsForTag(tag, mLimit, mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
            db.close();
        }
    }

    public List<ApplicationBean> getApplicationBeanList() {
        return applicationBeanList;
    }
}
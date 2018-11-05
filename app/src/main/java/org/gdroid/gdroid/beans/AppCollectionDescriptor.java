package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Room;
import android.content.Context;

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
        this(context,name, 10);
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
        }
        else if (collectionName.equals("Recently updated"))
        {
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getLastUpdated(mLimit,mOffset);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
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
        }
    }

    public List<ApplicationBean> getApplicationBeanList() {
        return applicationBeanList;
    }
}
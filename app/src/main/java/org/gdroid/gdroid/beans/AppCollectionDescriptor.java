package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * wraps a name of a collection and a list of apps to be contained in the collection, to be shown in the UI
 */
public class AppCollectionDescriptor {
    private Context mContext;
    private String name;
    private List<ApplicationBean> applicationBeanList;

    public AppCollectionDescriptor(Context context, String name) {
        this.mContext = context;
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

            ApplicationBean[] appsInDb = db.appDao().getLastAdded(10);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
        }
        else if (collectionName.equals("Recently updated"))
        {
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getLastUpdated(10);

            applicationBeanList.clear();
            for (ApplicationBean ab: appsInDb ) {
                applicationBeanList.add(ab);
            }
        }
        else if (collectionName.startsWith("cat:"))
        {
            String cat = collectionName.replace("cat:","");
            AppDatabase db = AppDatabase.get(mContext);
            ApplicationBean[] appsInDb = db.appDao().getAllAppsForCategory(cat, 10);

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
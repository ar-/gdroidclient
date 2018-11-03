package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ApplicationBean.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String db="gdroiddb";
    public abstract SimpleApplicationDao appDao();
}

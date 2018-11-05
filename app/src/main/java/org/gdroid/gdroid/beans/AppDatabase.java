package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ApplicationBean.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public static final String db="gdroiddb";
    public abstract SimpleApplicationDao appDao();

    public static final AppDatabase get(Context context)
    {
        return Room.databaseBuilder(context, AppDatabase.class, AppDatabase.db).fallbackToDestructiveMigration().allowMainThreadQueries().build();
    }
}

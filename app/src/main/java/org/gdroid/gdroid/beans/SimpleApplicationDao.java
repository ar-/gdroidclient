package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface SimpleApplicationDao {

    @Query("SELECT * FROM ApplicationBean")
    public ApplicationBean[] loadAllApplicationBeans();

    @Query("SELECT * FROM ApplicationBean ORDER BY lastupdated DESC LIMIT :limit")
    public ApplicationBean[] getLastUpdated(int limit);

    @Query("SELECT * FROM ApplicationBean ORDER BY added DESC LIMIT :limit")
    public ApplicationBean[] getLastAdded(int limit);

    @Query("SELECT * FROM ApplicationBean WHERE id = :id LIMIT 1")
    public ApplicationBean getApplicationBean(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertApplicationBeans(ApplicationBean... ApplicationBeans);

    @Update
    public void updateApplicationBeans(ApplicationBean... ApplicationBeans);

    @Delete
    public void deleteApplicationBeans(ApplicationBean... ApplicationBeans);


}

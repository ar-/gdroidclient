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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCategories(CategoryBean... CategoryBeans);

    @Query("DELETE FROM CategoryBean WHERE appId = :appId")
    public void deleteCategoriesForApp(String appId);

    @Query("SELECT * FROM CategoryBean WHERE appId = :appId")
    public CategoryBean[] getAllCategoriesForApp(String appId);

    // TODO ask the user what to ORDER BY in the settings
    @Query("SELECT a.* FROM CategoryBean c LEFT JOIN ApplicationBean a ON (c.appId = a.id) WHERE c.catName = :catName" +
            " ORDER BY a.lastupdated DESC, a.added ASC LIMIT :limit")
    public ApplicationBean[] getAllAppsForCategory(String catName, int limit);

    @Query("SELECT DISTINCT catName FROM CategoryBean")
    public String[] getAllCategoryNames();
}

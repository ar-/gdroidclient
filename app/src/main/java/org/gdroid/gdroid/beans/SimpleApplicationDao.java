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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface SimpleApplicationDao {

    @Query("SELECT * FROM ApplicationBean")
    public ApplicationBean[] getAllApplicationBeans();

    @Query("SELECT * FROM ApplicationBean ORDER BY lastupdated DESC LIMIT :limit OFFSET :offset")
    public ApplicationBean[] getLastUpdated(int limit, int offset);

    @Query("SELECT * FROM ApplicationBean ORDER BY added DESC LIMIT :limit OFFSET :offset")
    public ApplicationBean[] getLastAdded(int limit, int offset);

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
            " ORDER BY a.lastupdated DESC, a.added ASC LIMIT :limit OFFSET :offset")
    public ApplicationBean[] getAllAppsForCategory(String catName, int limit, int offset);

    @Query("SELECT * FROM ApplicationBean WHERE name like :ss " +
            " ORDER BY lastupdated DESC, added ASC LIMIT :limit OFFSET :offset")
    public ApplicationBean[] getAllAppsForSearchString(String ss, int limit, int offset);

    @Query("SELECT DISTINCT catName FROM CategoryBean")
    public String[] getAllCategoryNames();
}

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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(indices = {@Index("name")
        ,@Index("lastupdated")
        ,@Index("added")
})

public class ApplicationBean {

    @PrimaryKey
    @NonNull
    public String id;
    public long added;
    public long lastupdated;
    public String name;
    public String summary;
    public String icon;
    public float stars;
    public String desc;
    public String license;
    @Ignore
    public List<String> categories;
    public String web;
    public String source;
    public String tracker;
    public String changelog;
    public String bitcoin;
    public String liberapay;
    public String marketversion;
    public String marketvercode;
    public String antifeatures;
    public String author;
    public String email;
    public String permissions;
    public String apkname;
    public String whatsNew;
    public String featureGraphic;

    public ApplicationBean() {
    }

    public ApplicationBean(String id) {
        this.id = id;
    }

//    public ApplicationBean(String name, float stars, int thumbnail) {
//        this.name = name;
//        this.stars = stars;
//        this.thumbnail = thumbnail;
//    }

    /**
     * Gets a list of catgories, if the bean has jsut been filled by XML parser.
     * Otherwise this method will not work.
     * If this bean has been retireved later, eg. from the DB, the categoreis must also be fetched from the separate DAO.
     * @return
     */
    public List<CategoryBean> getCategoryList()
    {
        ArrayList<CategoryBean> ret = new ArrayList<>(2);
        if (categories != null)
        {
            for (String cat:categories) {
                CategoryBean cb = new CategoryBean(cat,id);
                ret.add(cb);
            }
        }
        return ret;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public float getStars() {
//        return stars;
//    }
//
//    public void setStars(float stars) {
//        this.stars = stars;
//    }

//    public int getThumbnail() {
//        return thumbnail;
//    }
//
//    public void setThumbnail(int thumbnail) {
//        this.thumbnail = thumbnail;
//    }
}
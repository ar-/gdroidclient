/*
 * Copyright (C) 2018,2019 Andreas Redmer <ar-gdroid@abga.be>
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
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

@Entity(indices = {@Index("name")
        ,@Index("lastupdated")
        ,@Index("added")
        ,@Index("author") // should be "lower(trim(author))" , which is not supported by room :-(
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

    /**
     * List of screenshots, separated by semicolon (since comma can be in filename),
     * points relatively to the F-Droid repo, but can be an absolute URL as well.
     */
    public String screenshots;
    public String metricsJson;
    public int metriccount;
    @Ignore
    public List<String> tags;

    public boolean isHidden = false;

    /**
     * List of neighbours, separated by semicolon (since comma can be in id)
     */
    public String neighbours;

    public String hash;
    public int size;



    public ApplicationBean() {
    }

    @Ignore
    public ApplicationBean(String id) {
        this.id = id;
    }

    /**
     * Gets a list of catgories, if the bean has just been filled by parser.
     * Otherwise this method will not work.
     * If this bean has been retireved later, eg. from the DB, the categoreis must also be fetched from the separate DAO.
     * @return
     */
    public List<CategoryBean> getCategoryList()
    {
        ArrayList<CategoryBean> ret = new ArrayList<>();
        if (categories != null)
        {
            for (String cat:categories) {
                CategoryBean cb = new CategoryBean(cat,id);
                ret.add(cb);
            }
        }
        return ret;
    }

    public List<TagBean> getTagList()
    {
        ArrayList<TagBean> ret = new ArrayList<>();
        if (tags != null)
        {
            for (String cat:tags) {
                TagBean tb = new TagBean(cat,id);
                ret.add(tb);
            }
        }
        return ret;
    }

    public List<String> getScreenshotList()
    {
        ArrayList<String> ret = new ArrayList<>();
        if (screenshots != null)
        {
            for (String s:screenshots.split(";")) {
                ret.add(s);
            }
        }
        return ret;
    }

    public List<String> getNeighbours()
    {
        ArrayList<String> ret = new ArrayList<>();
        if (neighbours != null)
        {
            for (String s:neighbours.split(";")) {
                ret.add(s);
            }
        }
        return ret;
    }

    public List<Pair<String,Integer>> getNeighboursWithSimilarity()
    {
        ArrayList<Pair<String,Integer>> ret = new ArrayList<>();
        if (neighbours != null)
        {
            String[] split = neighbours.split(";");
            int i = split.length+1;
            for (String s:split) {
                ret.add(new Pair<>(s, i--));
            }
        }
        return ret;
    }

    /**
     * can be used to remove all unneeded attempts to load XML files into image views
     * @return the URL of the icon if it is a renderable image
     */
    public String getAppIconIfValid()
    {
        final String ls = icon.toLowerCase();
        if (ls.endsWith("png") ||ls.endsWith("jpg") ||ls.endsWith("jpeg"))
            return icon;
        return "";

    }

    @Override
    public String toString() {
        //overridden for better debug output
        if (name != null)
            return name + " " +super.toString();
        return super.toString();
    }
}
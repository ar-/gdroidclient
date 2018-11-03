package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(indices = {@Index("name")
        ,@Index("lastupdated")
        ,@Index("added")
})

public class ApplicationBean {

    @PrimaryKey
    @NonNull
    public String id;
    public String added;
    public String lastupdated;
    public String name;
    public String summary;
    public float stars;

    @Ignore
    public int thumbnail;

    public ApplicationBean() {
    }

    public ApplicationBean(String id) {
        this.id = id;
    }

    public ApplicationBean(String name, float stars, int thumbnail) {
        this.name = name;
        this.stars = stars;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
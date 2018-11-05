package org.gdroid.gdroid.beans;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"catName", "appId"},
        indices = {@Index("catName")
        ,@Index("appId")
})

public class CategoryBean {
    @NonNull
    public String catName;
    @NonNull
    public String appId;

    public CategoryBean() {
    }

    public CategoryBean(@NonNull String catName, @NonNull String appId) {
        this.catName = catName;
        this.appId = appId;
    }
}

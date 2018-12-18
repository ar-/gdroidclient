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
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"tagName", "appId"},
        indices = {@Index("tagName")
        ,@Index("appId")
})

public class TagBean {
    @NonNull
    public String tagName;
    @NonNull
    public String appId;

    public TagBean() {
    }

    public TagBean(@NonNull String catName, @NonNull String appId) {
        this.tagName = catName;
        this.appId = appId;
    }
}

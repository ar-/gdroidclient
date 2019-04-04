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

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {ApplicationBean.class,CategoryBean.class,TagBean.class}, version = 19)
public abstract class AppDatabase extends RoomDatabase {
    public static final String db="gdroiddb";
    public abstract SimpleApplicationDao appDao();

    public static final AppDatabase get(Context context)
    {
        return Room.databaseBuilder(context, AppDatabase.class, AppDatabase.db)
                .addMigrations(MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public static final Migration MIGRATION_16_17 = new Migration(16, 17) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ApplicationBean ADD COLUMN hash TEXT");
        }
    };

    public static final Migration MIGRATION_17_18 = new Migration(17, 18) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ApplicationBean ADD COLUMN `size` INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_18_19 = new Migration(18, 19) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ApplicationBean ADD COLUMN `versionsJson` TEXT");
        }
    };

}

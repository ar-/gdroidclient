/*
 * Copyright (C) 2019 Andreas Redmer <ar-gdroid@abga.be>
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

package org.gdroid.gdroid.repos;

import android.text.TextUtils;

import org.gdroid.gdroid.beans.ApplicationBean;

public class Repo {
    public String getBaseUrl()
    {
//        return "https://f-droid.org/repo";
        return "https://fdroid.tetaneutral.net/fdroid/repo";
    }

    public String getAppIconUrlIfValid(ApplicationBean ab)
    {
        final String iu = ab.getAppIconIfValid();
        if (TextUtils.isEmpty(iu))
        {
            return "http://"; // this will make the http request invalid and fail fast, to load the error image quickly
        }
        return getBaseUrl()+"/icons-640/"+ iu;
    }
}

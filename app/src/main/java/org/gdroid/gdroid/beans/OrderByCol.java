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

package org.gdroid.gdroid.beans;

public enum OrderByCol {
    // these are some fields of the ApplicationBean, some views can be ordered by these columns
    // do not rename ;)
    added,
    lastupdated,
    name,
    stars;

    /**
     * Unfortunately room doesn't support direct keyword injections, except with 'raw queries'.
     * which invalidate the whole concept of the room dao.
     * So the function output of this will eventually be used by a java comparator.
     * @param ascending
     * @return
     */
    public String toSQLString(boolean ascending)
    {
        return name() + (ascending ? " ASC" : " DESC");
    }
}

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

import java.util.Comparator;

public class AppBeanNameComparator implements Comparator<ApplicationBean> {
    @Override
    public int compare(ApplicationBean o1, ApplicationBean o2) {
        if (o1 ==null || o2 ==null)
            return 0;
        if (o1.name ==null || o2.name ==null)
            return 0;

        return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
    }
}

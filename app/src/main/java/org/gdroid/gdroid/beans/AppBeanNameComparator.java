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

import android.content.Context;

import org.gdroid.gdroid.Util;

import java.util.Comparator;

public class AppBeanNameComparator implements Comparator<ApplicationBean> {


    private final Context mContext;
    private final OrderByCol mOrderByCol;
    private final boolean mAscending;
    private final boolean mUpdatableFirst;

    public AppBeanNameComparator(Context context, OrderByCol orderByCol, boolean ascending) {
        this(context,orderByCol,ascending,false);
    }

    public AppBeanNameComparator(Context context, OrderByCol orderByCol, boolean ascending, boolean updatableFirst) {
        this.mContext = context;
        this.mOrderByCol = orderByCol;
        this.mAscending = ascending;
        this.mUpdatableFirst = updatableFirst;
    }

    @Override
    public int compare(ApplicationBean o1, ApplicationBean o2) {
        if (o1 ==null || o2 ==null)
            return 0;
        if (o1.name ==null || o2.name ==null)
            return 0;

        // this makes the comparator very slow, it must only be used on the myapps tab
        if (mUpdatableFirst) {
            final boolean ud1 = Util.isAppUpdateable(mContext, o1);
            final boolean ud2 = Util.isAppUpdateable(mContext, o2);

            if (ud1 != ud2)
                // compare in inverese order since true comes before false for this case
                return Boolean.valueOf(ud2).compareTo(ud1);
        }
        switch (mOrderByCol)
        {
            case name:
                if (mAscending)
                    return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
                else
                    return o2.name.toLowerCase().compareTo(o1.name.toLowerCase());
            case stars:
                if (mAscending)
                    return Float.compare(o1.stars,o2.stars);
                else
                    return Float.compare(o2.stars,o1.stars);
            case lastupdated:
                if (mAscending)
                    return compareLong(o1.lastupdated,o2.lastupdated);
                else
                    return compareLong(o2.lastupdated,o1.lastupdated);
            case added:
                if (mAscending)
                    return compareLong(o1.added,o2.added);
                else
                    return compareLong(o2.added,o1.added);
                default:
                    return 0; // can't happen
        }
    }

    // stolen from Long.java because this highly sophisticated function requires API level 19 - no way you can do that in 14 !!
    public static int compareLong(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

}

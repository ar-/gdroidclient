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

package org.gdroid.gdroid;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MetaMetric {
    // list of [metricvalue,weigth]
    List<Double> values;
    List<Integer> weights;
    public MetaMetric()
    {
        values = new ArrayList<>(3);
        weights = new ArrayList<>(3);
    }

    /**
     *
     * @param val a normalized percentage between 0.0 and 1.0
     * @param weight a positive in > 0
     */
    public void addMetric (double val, int weight)
    {
        if (weight<1)
            weight=1;

        if (val<0.0)
            val =0.0;

        if (val>1.0)
            val =1.0;

        values.add(val);
        weights.add(weight);
    }

    /**
     *
     * @return the percentage (between 0.0. and 1.0) of all weighted metrics combined
     */
    public double getPercentage()
    {
        if (weights.isEmpty())
            return 0.0;
        int weightSum=0;
        double valSum=0.0;

        for (int i = 0; i<weights.size() ; i++)
        {
            double v = values.get(i);
            int w = weights.get(i);
            valSum += v*(double)w;
            weightSum += w;
        }

        if (weightSum == 0)
            return 0.0; // cannot happen, but bsts

        return valSum / (double)weightSum;
    }

    public double getScaledValue(double maxVal)
    {
        final double ret = getPercentage() * maxVal;
        if (ret > maxVal)
            return maxVal;
        if (ret < 0.0)
            return 0.0;
        return ret;
    }

    public void clear ()
    {
        values.clear();
        weights.clear();
    }

    public int countMetrics() {
        return values.size();
    }
}

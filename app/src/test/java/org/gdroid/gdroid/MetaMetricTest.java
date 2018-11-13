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

import org.junit.Test;

import static org.junit.Assert.*;


public class MetaMetricTest {
    @Test
    public void check0Met() {
        MetaMetric mm = new MetaMetric();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);
    }

    @Test
    public void check1Met() {
        MetaMetric mm = new MetaMetric();
        double v1 = 0.56789;
        int w1 = 42;
        mm.addMetric(v1,w1);
        assertEquals (v1 ,  mm.getPercentage() ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);

        v1 = 0.5;
        mm.addMetric(v1,w1);
        assertEquals (2.5 ,  mm.getScaledValue(5) ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);

        v1 = 0.1;
        mm.addMetric(v1,w1);
        assertEquals (0.5 ,  mm.getScaledValue(5) ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);

        v1 = 0.0;
        mm.addMetric(v1,w1);
        assertEquals (0 ,  mm.getScaledValue(5) ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);

        v1 = -3330.0;
        mm.addMetric(v1,w1);
        assertEquals (0 ,  mm.getScaledValue(5) ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);

        v1 = 1.0;
        mm.addMetric(v1,w1);
        assertEquals (5 ,  mm.getScaledValue(5) ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);

        v1 = 13333.0;
        mm.addMetric(v1,w1);
        assertEquals (5 ,  mm.getScaledValue(5) ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);
    }

    @Test
    public void check2Met() {
        MetaMetric mm = new MetaMetric();
        double v1 = 0.5;
        int w1 = 1;
        double v2 = 0.25;
        int w2 = 1;
        mm.addMetric(v1,w1);
        mm.addMetric(v2,w2);
        assertEquals ( 0.375,  mm.getPercentage() ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);

        mm.addMetric(0.1,1);
        mm.addMetric(1.0,9);
        assertEquals ( 0.91,  mm.getPercentage() ,0.000001);
        mm.clear();
        assertEquals (0.0 ,  mm.getPercentage() ,0.000001);
    }
}
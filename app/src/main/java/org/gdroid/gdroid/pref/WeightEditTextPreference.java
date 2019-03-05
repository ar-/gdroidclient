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

package org.gdroid.gdroid.pref;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class WeightEditTextPreference extends EditTextPreference {

    public WeightEditTextPreference(Context context) {
        super(context);
    }

    public WeightEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeightEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        int persistedInt = getPersistedInt(1);
        if (persistedInt <0)
        {
            persistedInt=0;
        }
        if (persistedInt>10)
        {
            persistedInt=10;
        }

        return String.valueOf(persistedInt);
    }

    @Override
    protected boolean persistString(String value) {
        Integer persistedInt = 1;
        try {
            persistedInt = Integer.valueOf(value);
        }
        catch (Throwable t)
        {

        }
        if (persistedInt <0)
        {
            persistedInt=0;
        }
        if (persistedInt>10)
        {
            persistedInt=10;
        }
        return persistInt(persistedInt);
    }
}
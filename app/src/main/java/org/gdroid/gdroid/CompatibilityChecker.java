/*
 * Copyright (C) 2019 Andreas Redmer <ar-gdroid@abga.be>
 * Copyright (C) 2016-2018 F-Droid Project
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

import android.os.Build;
import android.support.annotation.Nullable;

import java.util.List;

// Call getIncompatibleReasons(apk) on an instance of this class to
// find reasons why an apk may be incompatible with the user's device.
public class CompatibilityChecker {

    private final String[] cpuAbis;

    public CompatibilityChecker() {
        cpuAbis = Util.getAbis();
    }

    private boolean compatibleApi(@Nullable List<String> nativecode) {
        if (nativecode == null) {
            return true;
        }

        for (final String cpuAbi : cpuAbis) {
            for (String code : nativecode) {
                if (code.equals(cpuAbi)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCompatible(int minSdkVersion, int maxSdkVersion, List<String> nativecode) {
        if (Build.VERSION.SDK_INT < minSdkVersion) {
            return false;
        } else if (Build.VERSION.SDK_INT > maxSdkVersion) {
            return false;
        }

        return compatibleApi(nativecode);
    }
}

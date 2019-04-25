/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.utils;

import com.github.benchdoos.weblocopener.update.AppVersion;
import com.github.benchdoos.weblocopener.utils.version.Version;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Eugene Zrazhevsky on 03.11.2016.
 */
public class Internal {
    /**
     * Compares two version strings.
     * <p>
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     * The result is a positive integer if str1 is _numerically_ greater than str2.
     * The result is zero if the strings are _numerically_ equal.
     * @apiNote It does not work if "1.10" is supposed to be equal to "1.10.0".
     */
    public static int versionCompare(String str1, String str2) {
        if (str1 == null) {
            str1 = "0.0";
        }
        if (str2 == null) {
            str2 = "0.0";
        }
        String[] values1 = str1.split("\\.");
        String[] values2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < values1.length && i < values2.length && values1[i].equals(values2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < values1.length && i < values2.length) {
            int diff = Integer.valueOf(values1[i]).compareTo(Integer.valueOf(values2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(values1.length - values2.length);
    }

    public static VersionCompare versionCompare(@NotNull AppVersion serverVersion) {
        AppVersion currentAppVersion = CoreUtils.getCurrentAppVersion();

        final int compare = new Version(serverVersion).compareTo(new Version(currentAppVersion));
        if (compare < 0) {
            System.out.println("CURRENT? VERSION IS NEWER");
            return VersionCompare.CURRENT_VERSION_IS_NEWER;
        } else if (compare > 0) {
            System.out.println("CURRENT? VERSION IS NEWER");
            return VersionCompare.SERVER_VERSION_IS_NEWER;
        } else {
            System.out.println("VERSIONS ARE EQUAL");
            return VersionCompare.VERSIONS_ARE_EQUAL;
        }
    }

    public enum VersionCompare {
        SERVER_VERSION_IS_NEWER,
        CURRENT_VERSION_IS_NEWER,
        VERSIONS_ARE_EQUAL
    }
}

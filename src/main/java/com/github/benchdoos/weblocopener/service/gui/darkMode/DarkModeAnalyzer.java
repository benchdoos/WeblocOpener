/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
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

package com.github.benchdoos.weblocopener.service.gui.darkMode;

import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

public class DarkModeAnalyzer {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    private static Calendar getCalendarForTime(String s) {
        final String[] time = s.split(":");
        final String hour = time[0];
        final String minute = time[1];

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        cal.set(Calendar.MINUTE, Integer.parseInt(minute));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private static TimeRange getEndTimeRange(String[] split) {
        Calendar endBegin = getCalendarForTime(split[0]);
        Calendar endEnd = getCalendarForTime(split[1]);
        endEnd.add(Calendar.DATE, 1);

        return new TimeRange(endBegin.getTime(), endEnd.getTime());
    }

    private static TimeRange getStartTimeRange(String[] split) {
        Calendar startBegin = getCalendarForTime(split[0]);
        startBegin.add(Calendar.DATE, -1);

        Calendar startEnd = getCalendarForTime(split[1]);
        return new TimeRange(startBegin.getTime(), startEnd.getTime());
    }

    public static boolean isDarkModeEnabledByNotDefaultData(String value) {
        final String[] split = value.split(";");
        if (split[0].contains(":")) {
            return isDarkModeEnabledByTimeRange(split);
        }
        return true;
    }

    private static boolean isDarkModeEnabledByTimeRange(String[] split) {
        final TimeRange startTimeRange = getStartTimeRange(split);
        final TimeRange endTimeRange = getEndTimeRange(split);

        log.debug("Dark mode ranges:"
                + " begins: " + startTimeRange

                + " ends: " + endTimeRange);
        final Date nowTime = Calendar.getInstance().getTime();
        log.debug("Today: " + nowTime);

        if (startTimeRange.isInRange(nowTime)) {
            return true;
        } else return endTimeRange.isInRange(nowTime);
    }
}

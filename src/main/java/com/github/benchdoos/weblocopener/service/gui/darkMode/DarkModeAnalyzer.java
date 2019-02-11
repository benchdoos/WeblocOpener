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

import java.io.IOException;
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

    public static Object getDarkModeValue(String s) {
        final String[] split = s.split(";");
        if (split[0].contains("|")) {
            final Location location = getLocation(s);
            return new DarkModeValue(location);
        } else {
            return new DarkModeValue(getStartTimeRange(split), getEndTimeRange(split));
        }
    }

    private static TimeRange getEndTimeRange(String[] split) {
        Calendar endBegin = getCalendarForTime(split[0]);
        Calendar endEnd = getCalendarForTime(split[1]);
        endEnd.add(Calendar.DATE, 1);

        return new TimeRange(endBegin.getTime(), endEnd.getTime());
    }

    private static Location getLocation(String value) {
        final String[] split = value.split("\\|");
        final String address = split[0];
        final String locationCoordinates = split[1];

        String[] locationSplit = locationCoordinates.split(";");
        double longitude = Double.valueOf(locationSplit[0]);
        double latitude = Double.valueOf(locationSplit[1]);

        return new Location(longitude, latitude, address);
    }

    private static TimeRange getStartTimeRange(String[] split) {
        Calendar startBegin = getCalendarForTime(split[0]);
        startBegin.add(Calendar.DATE, -1);

        Calendar startEnd = getCalendarForTime(split[1]);
        return new TimeRange(startBegin.getTime(), startEnd.getTime());
    }

    private static boolean isDarkModeEnabledByLocation(String value) {
        Location location = getLocation(value);
        try {
            final DarkModeValue darkModeValue = getDarkModeValue(location);
            final Calendar instance = Calendar.getInstance();
            Date now = instance.getTime();

            if (darkModeValue.getPrevious().isInRange(now)) {
                return true;
            } else return darkModeValue.getNext().isInRange(now);
        } catch (IOException e) {
            log.warn("Could not load sunrise/sunset info from location: {}", location, e);
        }
        //get from api
        return false;
    }

    private static DarkModeValue getDarkModeValue(Location location) throws IOException {
        final SunManager sunManager = new SunManager(location);
        final TimeRange byLocationYesterday = sunManager.getDarkModeValueByLocation("yesterday");
        final TimeRange byLocationToday = sunManager.getDarkModeValueByLocation("today");
        final TimeRange byLocationTomorrow = sunManager.getDarkModeValueByLocation("tomorrow");

        final TimeRange realBeginningTimeRange = new TimeRange(byLocationYesterday.getEnd(), byLocationToday.getStart());
        final TimeRange realEndingTimeRange = new TimeRange(byLocationToday.getEnd(), byLocationTomorrow.getStart());
        DarkModeValue value = new DarkModeValue(realBeginningTimeRange, realEndingTimeRange);
        value.setLocation(location);
        return value;
    }

    public static boolean isDarkModeEnabledByNotDefaultData(String value) {
        final String[] split = value.split(";");
        if (split[0].contains("|")) {
            return isDarkModeEnabledByLocation(value);
        } else {
            return isDarkModeEnabledByTimeRange(split);
        }
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

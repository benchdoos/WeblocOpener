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

import com.github.benchdoos.weblocopener.utils.ConnectionUtils;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SunManager {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    private Location location;

    public SunManager(Location location) {

        this.location = location;
    }

    private HttpsURLConnection createConnection(String day) {
        try {
            final HttpsURLConnection connection = getConnection(day);

            return ConnectionUtils.getNeededHttpsURLConnection(connection);
        } catch (IOException e) {
            log.warn("Could not establish connection", e);
            return null;
        }
    }

    private HttpsURLConnection getConnection(String day) throws IOException {
        URL url = new URL(
                "https://api.sunrise-sunset.org/json?" +
                        "lat=" + location.getLatitude() +
                        "&lng=" + location.getLongitude() +
                        "&formatted=0" +
                        "&date=" + day);
        log.debug("Creating connection");

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(500);
        return connection;
    }

    TimeRange getDarkModeValueByLocation(String day) throws IOException {
        final HttpsURLConnection connection = createConnection(day);
        if (connection != null) {
            return getTimeRangeFromApi(connection);
        } else return null;

    }

    private TimeRange getTimeRangeFromApi(HttpsURLConnection connection) throws IOException {
        log.debug("Getting time range from sunrise-sunset.org");
        JsonObject root = ConnectionUtils.getJsonRootElementFromConnection(connection).getAsJsonObject();

        TimeRange value = getDarkModeValueFromJson(root);
        log.info("Got DarkModeValue from JSON: {}", value);
        return value;
    }


    private TimeRange getDarkModeValueFromJson(JsonObject root) {
        final JsonObject resultObject = root.getAsJsonObject().get("results").getAsJsonObject();
        final String sunriseTime = resultObject.get("sunrise").getAsString();
        final String sunsetTime = resultObject.get("sunset").getAsString();
        log.info("Found: sunrise: {} sunset: {}", sunriseTime, sunsetTime);

        DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        try {
            final Date sunriseDate = m_ISO8601Local.parse(sunriseTime);
            final Date sunsetDate = m_ISO8601Local.parse(sunsetTime);
            return new TimeRange(sunriseDate, sunsetDate);
        } catch (ParseException e) {
            log.warn("Could not parse sunrise: {} or sunset: {}", sunriseTime, sunsetTime, e);
            return null;
        }
    }
}

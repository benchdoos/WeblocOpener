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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class LocationManager {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    private String locationName;

    public LocationManager(String locationName) {
        this.locationName = locationName;
    }

    private HttpsURLConnection createConnection() {
        try {
            final HttpsURLConnection connection = getConnection();
            return ConnectionUtils.getNeededHttpsURLConnection(connection);
        } catch (IOException e) {
            log.warn("Could not establish connection", e);
            return null;
        }
    }


    private HttpsURLConnection getConnection() throws IOException {

        URL url = new URL("https://nominatim.openstreetmap.org/search/" +
                locationName.replaceAll(" ", "%20") +
                "?format=json&limit=5");
        log.debug("Creating connection");

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(500);
        return connection;
    }

    public Location[] getLocations() throws IOException {
        final HttpsURLConnection connection = createConnection();
        return getLocationData(connection);
    }

    private Location[] getLocationData(HttpsURLConnection connection) throws IOException {
        log.debug("Getting current server application version");
        final JsonArray asJsonArray = ConnectionUtils.getJsonRootElementFromConnection(connection).getAsJsonArray();
        return parseJsonObjectForLocations(asJsonArray);
    }

    private Location[] parseJsonObjectForLocations(JsonArray array) {
        System.out.println("size is: " + array.size());

        ArrayList<Location> locations = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JsonObject element = array.get(i).getAsJsonObject();
            locations.add(getLocationFromElement(element));
        }


        return locations.toArray(new Location[0]);
    }

    private Location getLocationFromElement(JsonObject element) {
        final JsonElement displayName = element.get("display_name");
        final JsonElement longitude = element.get("lon");
        final JsonElement latitude = element.get("lat");

        final Location location = new Location(longitude.getAsDouble(), latitude.getAsDouble());
        location.setAddress(displayName.getAsString());
        return location;
    }
}

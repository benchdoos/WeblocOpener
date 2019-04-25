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

package com.github.benchdoos.weblocopener.update;

import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.system.OperatingSystem;
import com.github.benchdoos.weblocopener.utils.version.ApplicationVersion;
import com.github.benchdoos.weblocopener.utils.version.Beta;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdaterManager {
    //todo add json deserializer
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    private static final int CONNECTION_TIMEOUT = 500;
    private static final String DEFAULT_ENCODING = "UTF-8";


    private static HttpsURLConnection createConnection(@NotNull String urlString) throws IOException {
        try {
            HttpsURLConnection connection;
            log.debug("Creating connection to github: {}", urlString);

            URL url = new URL(urlString);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            if (!connection.getDoOutput()) {
                connection.setDoOutput(true);
            }
            if (!connection.getDoInput()) {
                connection.setDoInput(true);
            }
            return connection;
        } catch (IOException e) {
            log.warn("Could not establish connection to github: {}", urlString, e);
            throw new IOException(e);
        }
    }

    public static Updater getUpdaterForCurrentOperatingSystem() {
        if (OperatingSystem.isWindows()) {
            return new WindowsUpdater();
        } else if (OperatingSystem.isUnix()) {
            return new UnixUpdater();
        } else return null;
    }

    private static ApplicationVersion formAppVersionFromLatestReleaseJson(String setupName, JsonObject root) {
        final String version = "tag_name";
        final String browser_download_url = "browser_download_url";
        final String assets = "assets";
        final String name = "name";
        final String size = "size";
        final String info = "body";
        final String prerelease = "prerelease"; //beta if true

        ApplicationVersion applicationVersion = new ApplicationVersion();


        applicationVersion.setVersion(root.getAsJsonObject().get(version).getAsString());
        applicationVersion.setUpdateInfo(root.getAsJsonObject().get(info).getAsString());
        applicationVersion.setUpdateTitle(root.getAsJsonObject().get(name).getAsString());
        final boolean isPreRelease = root.getAsJsonObject().get(prerelease).getAsBoolean();
        applicationVersion.setBeta(new Beta(isPreRelease ? 1 : 0));

        JsonArray asserts = root.getAsJsonArray(assets);
        for (JsonElement assert_ : asserts) {
            JsonObject userObject = assert_.getAsJsonObject();
            if (userObject.get(name).getAsString().equals(setupName)) {
                applicationVersion.setDownloadUrl(userObject.get(browser_download_url).getAsString());
                applicationVersion.setSize(userObject.get(size).getAsLong());
            }
        }
        return applicationVersion;
    }

    private static ApplicationVersion formAppVersionFromAllReleasesJson(String setupName, JsonArray array) {
        ApplicationVersion latestBeta = null;

        for (JsonElement jsonElement : array) {
            final ApplicationVersion applicationVersion = formAppVersionFromLatestReleaseJson(setupName, jsonElement.getAsJsonObject());
            if (applicationVersion.isBeta()) {
                latestBeta = applicationVersion;
                break;
            }
        }
        return latestBeta;
    }

    private static ApplicationVersion getLatestReleaseAppVersion(String setupName, HttpsURLConnection connection) throws IOException {
        log.debug("Getting current server application release version");
        String input;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODING));

        input = bufferedReader.readLine();

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(input).getAsJsonObject();

        return UpdaterManager.formAppVersionFromLatestReleaseJson(setupName, root);
    }

    private static ApplicationVersion getLatestBetaAppVersion(String setupName, HttpsURLConnection connection) throws IOException {
        log.debug("Getting latest server beta application version");
        String input;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODING));

        input = bufferedReader.readLine();

        JsonParser parser = new JsonParser();
        JsonArray root = parser.parse(input).getAsJsonArray();

        return UpdaterManager.formAppVersionFromAllReleasesJson(setupName, root);
    }

    static ApplicationVersion getLatestReleaseVersion(String setupName) {
        try {
            final HttpsURLConnection connection = UpdaterManager.createConnection(Updater.LATEST_RELEASE_URL);
            final ApplicationVersion serverLatestReleaseApplicationVersion = getLatestReleaseAppVersion(setupName, connection);

            log.info("Server application version: {}", serverLatestReleaseApplicationVersion);

            return serverLatestReleaseApplicationVersion;
        } catch (IOException e) {
            log.warn("Can not get release application version", e);
            return null;
        }
    }

    public static ApplicationVersion getLatestBetaVersion(String setupName) {
        try {
            final HttpsURLConnection connection = UpdaterManager.createConnection(Updater.ALL_RELEASES_URL);
            final ApplicationVersion serverLatestBetaApplicationVersion = getLatestBetaAppVersion(setupName, connection);

            log.info("Server latest beta application version: {}", serverLatestBetaApplicationVersion);

            return serverLatestBetaApplicationVersion;
        } catch (IOException e) {
            log.warn("Can not get latest beta application version", e);
            return null;
        }
    }
}

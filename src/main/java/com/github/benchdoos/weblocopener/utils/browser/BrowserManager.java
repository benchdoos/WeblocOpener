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

package com.github.benchdoos.weblocopener.utils.browser;

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.system.SystemUtils;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Eugene Zrazhevsky on 24.08.2017.
 */
public class BrowserManager {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());


    private static ArrayList<Browser> browserList = new ArrayList<>();


    public static ArrayList<Browser> getBrowserList() {
        return browserList;
    }

    private static ArrayList<Browser> getDefaultBrowsersList() {
        ArrayList<Browser> result = new ArrayList<>();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Browser.class, new BrowserDeserializer());
        Gson gson = builder.create();
        final String browserListJsonPath = getBrowserListJson();
        try {
            String content = IOUtils.toString(BrowserManager.class.getResourceAsStream(browserListJsonPath),
                    "UTF-8");
            JsonElement element = new JsonParser().parse(content);
            final JsonObject asJsonObject = element.getAsJsonObject();
            final JsonArray browsers = asJsonObject.getAsJsonArray("browsers");
            for (JsonElement el : browsers) {
                final Browser browser = gson.fromJson(el, Browser.class);
                result.add(browser);
            }
        } catch (IOException e) {
            log.warn("Could not load browsers list: {}", browserListJsonPath, e);
        } catch (Exception e) {
            log.warn("Could not load browsers list: {}, ignoring it", browserListJsonPath, e);
        }
        return result;
    }

    private static String getBrowserListJson() {
        final SystemUtils.OS currentOS = SystemUtils.getCurrentOS();
        return "/data/" + currentOS.toString().toLowerCase() + "/browsers.json";
    }

    public static void loadBrowserList() {
        loadBrowsersFromDefault(getDefaultBrowsersList());
    }

    private static void loadBrowsersFromDefault(ArrayList<Browser> list) {
        browserList = list;
        browserList.add(0, new Browser(
                Translation.getTranslatedString("CommonsBundle", "defaultBrowserName"),
                SettingsConstants.BROWSER_DEFAULT_VALUE));
        log.debug("Browsers count: " + browserList.size() + " " + browserList);

    }
}

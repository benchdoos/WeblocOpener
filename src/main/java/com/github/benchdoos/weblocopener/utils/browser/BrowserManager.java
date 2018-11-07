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

package com.github.benchdoos.weblocopener.utils.browser;

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.utils.Logging;
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

    private static String defaultBrowserName = "Default";

    public static ArrayList<Browser> getBrowserList() {
        return browserList;
    }

    private static ArrayList<Browser> getDefaultBrowsersList() {
        ArrayList<Browser> result = new ArrayList<>();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Browser.class, new BrowserDeserializer());
        Gson gson = builder.create();
        try {
            String content = IOUtils.toString(BrowserManager.class.getResourceAsStream("/data/browsers.json"),
                    "UTF-8");
            JsonElement element = new JsonParser().parse(content);
            final JsonObject asJsonObject = element.getAsJsonObject();
            final JsonArray browsers = asJsonObject.getAsJsonArray("browsers");
            for (JsonElement el : browsers) {
                final Browser browser = gson.fromJson(el, Browser.class);
                result.add(browser);
            }
        } catch (IOException e) {
            log.warn("Could not load browsers list", e);
        }
        return result;
    }

    private static void initTranslation() {
        Translation translation = new Translation("translations/CommonsBundle") {
            @Override
            public void initTranslations() {
                defaultBrowserName = messages.getString("defaultBrowserName");
            }
        };
        translation.initTranslations();
    }

    public static void loadBrowserList() {
        initTranslation();
        loadBrowsersFromDefault(getDefaultBrowsersList());
    }

    private static void loadBrowsersFromDefault(ArrayList<Browser> list) {
        browserList = list;
        browserList.add(0, new Browser(defaultBrowserName, SettingsConstants.BROWSER_DEFAULT_VALUE));
        log.debug("Browsers count: " + browserList.size() + " " + browserList);

    }
}
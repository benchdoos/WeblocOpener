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

package com.github.benchdoos.weblocopener.core;

import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;


public class Translation {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    private static volatile Locale locale;
    private final ResourceBundle messages;
    private final String bundlePath;
    private final String bundleName;

    public Translation(String bundleName) {
        locale = PreferencesManager.getLocale();

        this.bundleName = bundleName;
        this.bundlePath = "translations/" + bundleName;
        this.messages = getTranslation();
    }

    public static String getTranslatedString(String stringBundleName, String message) {
        try {
            final String bundlePath = "translations/" + stringBundleName;
            locale = PreferencesManager.getLocale();


            log.debug("[TRANSLATION] Locale: {} {}; Bundle: {}:[{}]", locale.getCountry(),
                    locale.getLanguage(), stringBundleName, message);

            final ResourceBundle bundle = ResourceBundle.getBundle(bundlePath, locale);

            return bundle.getString(message);
        } catch (Exception e) {
            log.warn("Could not translate  string: {}:[{}]", stringBundleName, message, e);
            throw new RuntimeException("Could not localize string: " + stringBundleName + ":[" + message + "]", e);
        }
    }

    public String getTranslatedString(String message) {
        try {
            log.debug("[TRANSLATION] Translating message: {}", message);
            return messages.getString(message);
        } catch (Exception e) {
            log.warn("Could not localize string: " + bundleName + ":[" + message + "]", e);
            throw new RuntimeException("Could not localize string: " + bundleName + ":[" + message + "]", e);

        }
    }

    private ResourceBundle getTranslation() {
        return ResourceBundle.getBundle(bundlePath, locale);
    }
}

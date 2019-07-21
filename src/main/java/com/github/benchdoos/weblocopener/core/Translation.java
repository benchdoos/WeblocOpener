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
import lombok.extern.log4j.Log4j2;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Log4j2
public class Translation {
    public static final Locale[] SUPPORTED_LOCALES = {
            new Locale("en", "EN"), new Locale("de", "DE"),
            new Locale("fr", "FR"), new Locale("it", "IT"),
            new Locale("ru", "RU")};

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
        } catch (MissingResourceException e) {
            log.warn("Could not find bundle {}:[{}] for locale: {}, trying to get necessary locale",
                    stringBundleName, message, locale);
            final Locale supportedLocale = getSupportedLocale(locale);

            log.info("For old locale: {} was found locale: {}", locale, supportedLocale);
            log.info("APPLYING new locale: {}", supportedLocale);
            locale = supportedLocale;
            PreferencesManager.setLocale(supportedLocale);
            return getTranslatedString(stringBundleName, message);
        } catch (Exception e) {
            log.warn("Could not translate string: {}:[{}] for locale: {}", stringBundleName, message, locale, e);
            throw new RuntimeException("Could not localize string: " + stringBundleName + ":[" + message + "]", e);
        }
    }

    private static Locale getSupportedLocale(Locale locale) {
        log.info("Trying to get current locale for {}", locale);

        for (Locale currentLocale : SUPPORTED_LOCALES) {
            try {
                if (locale.getLanguage().equalsIgnoreCase(currentLocale.getLanguage())) {
                    return locale;
                }
            } catch (Exception e) {
                log.warn("Could not get supported locale for locale: {}; current is: {}", locale, currentLocale, e);
            }
        }
        log.warn("Could not get locale, switching to en_EN");
        return new Locale("en", "EN");
    }

    public String getTranslatedString(String message) {
        try {
            log.debug("[TRANSLATION] Translating message: {}", message);
            return messages.getString(message);
        } catch (Exception e) {
            log.warn("Could not localize string: " + bundleName + ":[" + message + "]", e);
            return message;
        }
    }

    private ResourceBundle getTranslation() {
        return ResourceBundle.getBundle(bundlePath, locale);
    }
}

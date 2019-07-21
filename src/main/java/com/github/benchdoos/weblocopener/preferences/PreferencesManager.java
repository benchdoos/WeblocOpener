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

package com.github.benchdoos.weblocopener.preferences;

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.service.gui.darkMode.DarkModeAnalyzer;
import com.github.benchdoos.weblocopener.service.gui.darkMode.SimpleTime;
import com.github.benchdoos.weblocopener.service.links.Link;
import com.github.benchdoos.weblocopener.service.links.LinkFactory;
import lombok.extern.log4j.Log4j2;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.github.benchdoos.weblocopener.core.constants.ArgumentConstants.*;

/**
 * Created by Eugene Zrazhevsky on 19.11.2016.
 */
@Log4j2
public class PreferencesManager {
    public static final String KEY_AUTO_UPDATE = "auto_update_enabled";
    private static final String KEY_BETA_UPDATE_INSTALL = "install_beta_updates";
    private static final String KEY_OPEN_FOR_QR = "open_folder_for_qr";
    private static final String KEY_BROWSER = "browser";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String DEV_MODE_KEY = "dev_mode";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_CONVERTER_EXPORT_EXTENSION = "converter_export_extension";
    private static final String KEY_LOCALE = "locale";
    private static final String KEY_UNIX_OPENING_MODE = "unix_open_mode";
    private static final String KEY_LATEST_UPDATE_CHECK = "last_update_check";
    private static final String KEY_URL_PROCESSOR = "url_processor";
    private static final String KEY_OPEN_FOR_NEW_FILE = "open_folder_for_new_file";

    private static final Preferences PREFERENCES = Preferences.userRoot().node(ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME.toLowerCase());

    private static volatile SimpleTime lastDarkModeUpdateTime = null;
    private static volatile boolean lastDarkModeEnabled = false;

    public static String getBrowserValue() {
        final String value = PREFERENCES.get(KEY_BROWSER, SettingsConstants.BROWSER_DEFAULT_VALUE);
        if (value.isEmpty()) {
            return SettingsConstants.BROWSER_DEFAULT_VALUE;
        }
        return value;
    }

    public static void setBrowserValue(String callPath) {
        if (!callPath.isEmpty()) {
            PREFERENCES.put(KEY_BROWSER, callPath);
        }
    }

    public static void flushPreferences() {
        try {
            PREFERENCES.flush();
            log.info("Preferences flushed, new settings applied immediately");
        } catch (BackingStoreException e) {
            log.warn("Could not flush preferences immediately", e);
        }
    }

    public static String getConverterExportExtension() {
        final String s = PREFERENCES.get(KEY_CONVERTER_EXPORT_EXTENSION, SettingsConstants.CONVERTER_DEFAULT_EXTENSION);
        if (s.equalsIgnoreCase(ApplicationConstants.WEBLOC_FILE_EXTENSION)) {
            return SettingsConstants.CONVERTER_DEFAULT_EXTENSION;
        }
        return s;
    }

    public static void setConverterExportExtension(String value) {
        PREFERENCES.put(KEY_CONVERTER_EXPORT_EXTENSION, value);
    }

    private static String getDarkMode() {
        final String s = PREFERENCES.get(KEY_DARK_MODE, SettingsConstants.DARK_MODE_DEFAULT_VALUE.toString());
        if (s.equalsIgnoreCase(DARK_MODE.ALWAYS.toString())) {
            return DARK_MODE.ALWAYS.toString();
        } else if (s.equalsIgnoreCase(DARK_MODE.DISABLED.toString())) {
            return DARK_MODE.DISABLED.toString();
        } else {
            return s;
        }
    }

    public static void setDarkMode(String value) {
        PREFERENCES.put(KEY_DARK_MODE, value);
    }

    public static Locale getLocale() {
        final String s = PREFERENCES.get(KEY_LOCALE, SettingsConstants.LOCALE_DEFAULT_VALUE);

        try {
            if (s.equalsIgnoreCase(SettingsConstants.LOCALE_DEFAULT_VALUE)) {
                return Locale.getDefault();
            } else {
                final String[] split = s.split("_");
                return new Locale(split[0], split[1]);
            }
        } catch (Exception e) {
            return Locale.getDefault();
        }
    }

    public static void setLocale(Locale locale) {
        if (locale != null) {
            PREFERENCES.put(KEY_LOCALE, locale.toString());
        } else {
            PREFERENCES.put(KEY_LOCALE, SettingsConstants.LOCALE_DEFAULT_VALUE);
        }
    }

    public static Object getRealDarkMode() {
        final String s = PREFERENCES.get(KEY_DARK_MODE, SettingsConstants.DARK_MODE_DEFAULT_VALUE.toString());
        if (s.equalsIgnoreCase(DARK_MODE.ALWAYS.toString())) {
            return Boolean.TRUE;
        } else if (s.equalsIgnoreCase(DARK_MODE.DISABLED.toString())) {
            return Boolean.FALSE;
        } else {
            try {
                return DarkModeAnalyzer.getDarkModeValue(s);
            } catch (Exception e) {
                return SettingsConstants.DARK_MODE_DEFAULT_VALUE == DARK_MODE.ALWAYS;
            }
        }
    }

    public static boolean isAutoUpdateActive() {
        return PREFERENCES.getBoolean(KEY_AUTO_UPDATE, SettingsConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
    }

    public static void setAutoUpdateActive(boolean autoUpdateActive) {
        PREFERENCES.putBoolean(KEY_AUTO_UPDATE, autoUpdateActive);
    }

    public static boolean isBetaUpdateInstalling() {
        return PREFERENCES.getBoolean(KEY_BETA_UPDATE_INSTALL, SettingsConstants.IS_APP_BETA_UPDATE_INSTALLING_DEFAULT_VALUE);
    }

    public static void setBetaUpdateInstalling(boolean betaUpdatesInstalling) {
        PREFERENCES.putBoolean(KEY_BETA_UPDATE_INSTALL, betaUpdatesInstalling);
    }

    public static boolean isDarkModeEnabledNow() {
        switch (getDarkMode()) {
            case "ALWAYS":
                return true;
            case "DISABLED":
                return false;
            default: {
                try {
                    final Calendar instance = Calendar.getInstance();
                    final int startHour = instance.toInstant().atZone(ZoneId.systemDefault()).getHour();
                    final int startMinute = instance.toInstant().atZone(ZoneId.systemDefault()).getMinute();
                    SimpleTime time = new SimpleTime(startHour, startMinute);

                    if (lastDarkModeUpdateTime != null) {
                        if (lastDarkModeUpdateTime.equals(time)) {
                            return lastDarkModeEnabled;
                        } else {
                            lastDarkModeUpdateTime = time;
                            final boolean darkModeEnabledByNotDefaultData = DarkModeAnalyzer.isDarkModeEnabledByNotDefaultData(getDarkMode());
                            lastDarkModeEnabled = darkModeEnabledByNotDefaultData;
                            return darkModeEnabledByNotDefaultData;
                        }
                    } else {
                        lastDarkModeUpdateTime = time;
                        final boolean darkModeEnabledByNotDefaultData = DarkModeAnalyzer.isDarkModeEnabledByNotDefaultData(getDarkMode());
                        lastDarkModeEnabled = darkModeEnabledByNotDefaultData;
                        return darkModeEnabledByNotDefaultData;

                    }

                } catch (Exception e) {
                    return SettingsConstants.DARK_MODE_DEFAULT_VALUE == DARK_MODE.ALWAYS;
                }

            }
        }
    }

    public static boolean isDevMode() {
        return PREFERENCES.getBoolean(DEV_MODE_KEY, false);
    }

    public static boolean isNotificationsShown() {
        return PREFERENCES.getBoolean(KEY_NOTIFICATIONS, SettingsConstants.SHOW_NOTIFICATIONS_TO_USER);
    }

    public static void setNotificationsShown(boolean showNotifications) {
        PREFERENCES.putBoolean(KEY_NOTIFICATIONS, showNotifications);
    }

    public static boolean openFolderForQrCode() {
        return PREFERENCES.getBoolean(KEY_OPEN_FOR_QR, SettingsConstants.OPEN_FOLDER_FOR_QR_CODE);
    }

    public static void setOpenFolderForQrCode(boolean openFolderForQrCode) {
        PREFERENCES.putBoolean(KEY_OPEN_FOR_QR, openFolderForQrCode);
    }

    public static void setOpenFolderForNewFile(boolean value) {
        PREFERENCES.putBoolean(KEY_OPEN_FOR_NEW_FILE, value);
    }


    public static boolean openFolderForNewFile() {
        return PREFERENCES.getBoolean(KEY_OPEN_FOR_NEW_FILE, SettingsConstants.OPEN_FOLDER_FOR_NEW_FILE);
    }

    public static String getUnixOpeningMode() {
        return PREFERENCES.get(KEY_UNIX_OPENING_MODE, SettingsConstants.OPENER_UNIX_DEFAULT_SELECTOR_MODE);
    }

    public static void setUnixOpeningMode(String mode) {
        switch (mode) {
            case OPENER_OPEN_ARGUMENT:
            case OPENER_EDIT_ARGUMENT:
            case OPENER_QR_ARGUMENT:
            case OPENER_COPY_LINK_ARGUMENT:
            case OPENER_COPY_QR_ARGUMENT:
            case SettingsConstants.OPENER_UNIX_DEFAULT_SELECTOR_MODE:
                PREFERENCES.put(KEY_UNIX_OPENING_MODE, mode);
                break;
            default:
                log.warn("Can not save mode: {}, supported modes are: {},{},{},{},{},{}", mode,
                        OPENER_OPEN_ARGUMENT,
                        OPENER_EDIT_ARGUMENT,
                        OPENER_QR_ARGUMENT,
                        OPENER_COPY_LINK_ARGUMENT,
                        OPENER_COPY_QR_ARGUMENT, SettingsConstants.OPENER_UNIX_DEFAULT_SELECTOR_MODE);
                break;

        }
    }

    public static Date getLatestUpdateCheck() {
        final long aLong = PREFERENCES.getLong(KEY_LATEST_UPDATE_CHECK, 0);

        return new Date(aLong);
    }

    public static void setLatestUpdateCheck(Date date) {
        PREFERENCES.putLong(KEY_LATEST_UPDATE_CHECK, date.getTime());
    }

    public static Link getLink() {
        final String result = PREFERENCES.get(KEY_URL_PROCESSOR, SettingsConstants.URL_PROCESSOR.toString());
        return LinkFactory.getLinkByName(result);
    }

    public static void setLink(Link link) {
        PREFERENCES.put(KEY_URL_PROCESSOR, LinkFactory.getNameByLink(link));
    }

    public enum DARK_MODE {ALWAYS, DISABLED}
}


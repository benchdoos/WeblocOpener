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

package com.github.benchdoos.weblocopener.registry;

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.core.constants.StringConstants;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Created by Eugene Zrazhevsky on 19.11.2016.
 */
public class RegistryManager {
    private static final String KEY_INSTALL_LOCATION = "install_location";
    public static final String KEY_CURRENT_VERSION = "current_version";
    public static final String KEY_AUTO_UPDATE = "auto_update_enabled";
    private static final String KEY_APP_NAME = "name";
    private static final String KEY_URL_UPDATE_LINK = "url_update_info";
    private static final String DEV_MODE_KEY = "dev_mode";
    public static final String KEY_APP_ROOT_FOLDER_NAME = ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME;
    public final static String REGISTRY_APP_PATH
            = "SOFTWARE\\" + ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME + "\\";
    private static final String KEY_BROWSER = "browser";



    //    private static final Properties SETTINGS = new Properties();
    private static final Preferences PREFERENCES = Preferences.userRoot().node(ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME.toLowerCase());

    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());



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

    public static String getInstallLocationValue() throws RegistryCanNotReadInfoException {
        String value = PREFERENCES.get(KEY_INSTALL_LOCATION, SettingsConstants.BROWSER_DEFAULT_VALUE);
        if (value.isEmpty()) {
            throw new RegistryCanNotReadInfoException("Can not read Installed Location value: ");
        }
        if (!value.endsWith(File.separator)) {
            value = value + File.separator;
        }
        return value;
    }

    public static void setInstallLocationValue(String location) {
        PREFERENCES.put(KEY_INSTALL_LOCATION, location);
    }

    public static String getAppVersionValue() throws RegistryCanNotReadInfoException {
        String value = PREFERENCES.get(KEY_CURRENT_VERSION, CoreUtils.getApplicationVersionFullInformationString());
        if (value.isEmpty()) {
            throw new RegistryCanNotReadInfoException("Can not get app version value");
        }
        return value;
    }

    public static void setAppVersionValue(String version) {
        PREFERENCES.put(KEY_CURRENT_VERSION, version);
    }

    public static boolean isAutoUpdateActive() {
        return PREFERENCES.getBoolean(KEY_AUTO_UPDATE, SettingsConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
    }

    public static void setAutoUpdateActive(boolean autoUpdateActive) {
        PREFERENCES.putBoolean(KEY_AUTO_UPDATE, autoUpdateActive);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static String getAppNameValue() {
        return PREFERENCES.get(KEY_APP_NAME, ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME);
    }

    public static void setAppNameValue() {
        PREFERENCES.put(KEY_APP_NAME, ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static String getURLUpdateValue() {
        return PREFERENCES.get(KEY_URL_UPDATE_LINK, StringConstants.UPDATE_WEB_URL);
    }

    public static void setURLUpdateValue() {
        PREFERENCES.put(KEY_URL_UPDATE_LINK, StringConstants.UPDATE_WEB_URL);
    }


    public static boolean isDevMode() {
        return PREFERENCES.getBoolean(DEV_MODE_KEY, false);
    }


    /**
     * Sets default Settings if can not use registry;
     * It will help to prevent app from crash (if install location is not corrupt);
     */
    public static void setDefaultSettings() {
        log.info("[REGISTRY MANAGER] Setting default SETTINGS for app");
        PREFERENCES.put(KEY_CURRENT_VERSION, CoreUtils.getApplicationVersionFullInformationString());
        PREFERENCES.putBoolean(KEY_AUTO_UPDATE, SettingsConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
        PREFERENCES.put(KEY_APP_NAME, ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME);
        PREFERENCES.put(KEY_URL_UPDATE_LINK, StringConstants.UPDATE_WEB_URL);
    }
}



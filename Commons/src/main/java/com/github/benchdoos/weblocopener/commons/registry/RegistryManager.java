package com.github.benchdoos.weblocopener.commons.registry;

//import com.sun.deploy.util.WinRegistry;

import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.utils.Logging;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Properties;

/**
 * Created by Eugene Zrazhevsky on 19.11.2016.
 */
public class RegistryManager {
    public static final String KEY_INSTALL_LOCATION = "InstallLocation";
    public static final String KEY_CURRENT_VERSION = "CurrentVersion";
    public static final String KEY_AUTO_UPDATE = "AutoUpdateEnabled";
    public static final String KEY_APP_NAME = "Name";
    public static final String KEY_URL_UPDATE_LINK = "URLUpdateInfo";
    public static final String KEY_APP_ROOT_FOLDER_NAME = ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME;
    public final static String REGISTRY_APP_PATH
            = "SOFTWARE\\" + ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME + "\\";
    public static final String KEY_BROWSER = "Browser";


    private static final WinReg.HKEY APP_ROOT_HKEY = WinReg.HKEY_CURRENT_USER;

    private static final Properties SETTINGS = new Properties();

    private static final Logger log = Logger.getLogger(Logging.getCurrentClassName());


    public static void repealSettings() {
        log.info("[REGISTRY MANAGER] Setting SETTINGS to empty");
        SETTINGS.setProperty(KEY_CURRENT_VERSION, "");
        SETTINGS.setProperty(KEY_INSTALL_LOCATION, "");
        SETTINGS.setProperty(KEY_AUTO_UPDATE, "");
        SETTINGS.setProperty(KEY_BROWSER, "");
        SETTINGS.setProperty(KEY_APP_NAME, "");
        SETTINGS.setProperty(KEY_URL_UPDATE_LINK, "");
    }

    public static String getBrowserValue() {
        if (SETTINGS.getProperty(KEY_BROWSER) == null || SETTINGS.getProperty(KEY_BROWSER).isEmpty()) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                        REGISTRY_APP_PATH,
                        KEY_BROWSER);
                return value;
            } catch (Win32Exception e) {
                return ApplicationConstants.BROWSER_DEFAULT_VALUE;
            }
        } else {
            if (!SETTINGS.getProperty(KEY_BROWSER).equals(ApplicationConstants.BROWSER_DEFAULT_VALUE)) {
                return SETTINGS.getProperty(KEY_BROWSER);
            } else {
                return ApplicationConstants.BROWSER_DEFAULT_VALUE;
            }
        }
    }

    public static void setBrowserValue(String callPath) throws RegistryCanNotWriteInfoException {
        if (!callPath.isEmpty()) {
            RegistryManager.createRegistryEntry(KEY_BROWSER, callPath);
        }
    }

    public static String getInstallLocationValue() throws RegistryCanNotReadInfoException {

        if (SETTINGS.getProperty(KEY_INSTALL_LOCATION) == null || SETTINGS.getProperty(KEY_INSTALL_LOCATION).isEmpty()) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                        REGISTRY_APP_PATH,
                        KEY_INSTALL_LOCATION);
                if (!value.endsWith(File.separator)) {
                    value = value + File.separator;
                }
                SETTINGS.setProperty(KEY_INSTALL_LOCATION, value);
                return value;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not read Installed Location value : " +
                        "HKCU\\" + REGISTRY_APP_PATH + "" +
                        KEY_INSTALL_LOCATION,
                        e);
            }
        } else return SETTINGS.getProperty(KEY_INSTALL_LOCATION);
    }

    public static void setInstallLocationValue(String location) throws RegistryCanNotWriteInfoException {
        RegistryManager.createRegistryEntry(KEY_INSTALL_LOCATION, location);
    }

    public static String getAppVersionValue() throws RegistryCanNotReadInfoException {

        if (SETTINGS.getProperty(KEY_CURRENT_VERSION) == null || SETTINGS.getProperty(KEY_CURRENT_VERSION).isEmpty()) {
            try {
                String result = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                        REGISTRY_APP_PATH,
                        KEY_CURRENT_VERSION);
                SETTINGS.setProperty(KEY_CURRENT_VERSION, result);
                return result;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not get app version value", e);
            }
        } else return SETTINGS.getProperty(KEY_CURRENT_VERSION);
    }

    public static void setAppVersionValue(String version) throws RegistryCanNotWriteInfoException {
        createRegistryEntry(KEY_CURRENT_VERSION, version);
    }

    public static boolean isAutoUpdateActive() throws RegistryCanNotReadInfoException {
        if (SETTINGS.getProperty(KEY_AUTO_UPDATE) == null || SETTINGS.getProperty(KEY_AUTO_UPDATE).isEmpty()) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, KEY_AUTO_UPDATE);

                boolean result = Boolean.parseBoolean(value); //prevents
                SETTINGS.setProperty(KEY_AUTO_UPDATE, Boolean.toString(result));
                return result;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not read " + RegistryManager.KEY_AUTO_UPDATE + " value",
                        e);
            }
        } else return Boolean.parseBoolean(SETTINGS.getProperty(KEY_AUTO_UPDATE));
    }

    public static void setAutoUpdateActive(boolean autoUpdateActive) throws RegistryCanNotWriteInfoException {
        createRegistryEntry(KEY_AUTO_UPDATE, Boolean.toString(autoUpdateActive));
    }

    @SuppressWarnings("UnusedReturnValue")
    public static String getAppNameValue() throws RegistryCanNotReadInfoException {
        if (SETTINGS.getProperty(KEY_APP_NAME) == null || SETTINGS.getProperty(KEY_APP_NAME).isEmpty()) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                        REGISTRY_APP_PATH,
                        KEY_APP_NAME);
                SETTINGS.setProperty(KEY_APP_NAME, value);
                return value;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not read Installed Location value : " +
                        "HKLM\\" + REGISTRY_APP_PATH + "" + KEY_APP_NAME, e);
            }
        } else return SETTINGS.getProperty(KEY_APP_NAME);

    }

    public static void setAppNameValue() throws RegistryCanNotWriteInfoException {
        RegistryManager.createRegistryEntry(KEY_APP_NAME, ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static String getURLUpdateValue() throws RegistryCanNotReadInfoException {

        if (SETTINGS.getProperty(KEY_URL_UPDATE_LINK) == null || SETTINGS.getProperty(KEY_URL_UPDATE_LINK).isEmpty()) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                        REGISTRY_APP_PATH,
                        KEY_URL_UPDATE_LINK);
                SETTINGS.setProperty(KEY_URL_UPDATE_LINK, value);
                return value;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException(
                        "Can not read Installed Location value : " +
                                "HKLM\\" + REGISTRY_APP_PATH + "" + KEY_URL_UPDATE_LINK, e);
            }
        } else return SETTINGS.getProperty(KEY_URL_UPDATE_LINK);

    }

    public static void setURLUpdateValue() throws RegistryCanNotWriteInfoException {
        RegistryManager.createRegistryEntry(KEY_URL_UPDATE_LINK, ApplicationConstants.UPDATE_WEB_URL);
    }

    public static void createRegistryEntry(String path, String valueName, String value) throws
            RegistryCanNotWriteInfoException {
        try {
            SETTINGS.setProperty(valueName, value);
            Advapi32Util.registrySetStringValue(APP_ROOT_HKEY, path, valueName, value);
        } catch (Win32Exception e) {
            throw new RegistryCanNotWriteInfoException("Can not create entry at: "
                    + APP_ROOT_HKEY + "\\" + path + valueName
                    + " With value [" + value + "]", e);
        }
    }

    public static void createRegistryEntry(String valueName, String value) throws RegistryCanNotWriteInfoException {
        createRegistryEntry(REGISTRY_APP_PATH, valueName, value);
    }

    public static void createRootRegistryFolder(String path) throws RegistryCanNotWriteInfoException {
        if (!Advapi32Util.registryKeyExists(APP_ROOT_HKEY, path)) {
            try {
                Advapi32Util.registryCreateKey(APP_ROOT_HKEY, path);
            } catch (Win32Exception e) {
                throw new RegistryCanNotWriteInfoException("Can not create root folder on registry.", e);
            }
        }
    }

    /**
     * Sets default Settings if can not use registry;
     * It will help to prevent app from crash (if install location is not currupt);
     */
    public static void setDefaultSettings() {
        log.info("[REGISTRY MANAGER] Setting default SETTINGS for app");
        SETTINGS.setProperty(KEY_CURRENT_VERSION, ApplicationConstants.APP_VERSION);
        SETTINGS.setProperty(KEY_AUTO_UPDATE, Boolean.toString(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE));
        SETTINGS.setProperty(KEY_APP_NAME, ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME);
        SETTINGS.setProperty(KEY_URL_UPDATE_LINK, ApplicationConstants.UPDATE_WEB_URL);
    }
}



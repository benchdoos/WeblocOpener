package com.doos.settings_manager.registry;

//import com.sun.deploy.util.WinRegistry;

import com.doos.settings_manager.ApplicationConstants;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

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
    private static final WinReg.HKEY APP_ROOT_HKEY = WinReg.HKEY_CURRENT_USER;
    private final static String REGISTRY_APP_PATH = "SOFTWARE\\" + ApplicationConstants.APP_NAME + "\\";
    private static final Properties settings = new Properties();

    public static String getInstallLocationValue() throws RegistryCanNotReadInfoException {

        if (settings.getProperty(KEY_INSTALL_LOCATION) == null) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                                                                   REGISTRY_APP_PATH,
                                                                   KEY_INSTALL_LOCATION);
                if (!value.endsWith(File.separator)) {
                    value = value + File.separator;
                }
                settings.setProperty(KEY_INSTALL_LOCATION, value);
                return value;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not read Installed Location value : " +
                                                                  "HKLM\\" + REGISTRY_APP_PATH + "" + KEY_INSTALL_LOCATION,
                                                          e);
            }
        } else return settings.getProperty(KEY_INSTALL_LOCATION);
    }

    public static void setInstallLocationValue(String location) throws RegistryCanNotWriteInfoException {
        RegistryManager.createRegistryEntry(KEY_INSTALL_LOCATION, location);
    }

    public static String getAppVersionValue() throws RegistryCanNotReadInfoException {

        if (settings.getProperty(KEY_CURRENT_VERSION) == null) {
            try {
                String result = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                                                                    REGISTRY_APP_PATH,
                                                                    KEY_CURRENT_VERSION);
                settings.setProperty(KEY_CURRENT_VERSION, result);
                return result;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not get app version value", e);
            }
        } else return settings.getProperty(KEY_CURRENT_VERSION);
    }

    public static void setAppVersionValue(String version) throws RegistryCanNotWriteInfoException {
        createRegistryEntry(KEY_CURRENT_VERSION, version);
    }

    public static boolean isAutoUpdateActive() throws RegistryCanNotReadInfoException {
        if (settings.getProperty(KEY_AUTO_UPDATE) == null) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, KEY_AUTO_UPDATE);

                boolean result = Boolean.parseBoolean(value); //prevents
                settings.setProperty(KEY_AUTO_UPDATE, Boolean.toString(result));
                return result;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not read " + RegistryManager.KEY_AUTO_UPDATE + " value",
                                                          e);
            }
        } else return Boolean.parseBoolean(settings.getProperty(KEY_AUTO_UPDATE));
    }

    public static void setAutoUpdateActive(boolean autoUpdateActive) throws RegistryCanNotWriteInfoException {
        createRegistryEntry(KEY_AUTO_UPDATE, Boolean.toString(autoUpdateActive));
    }

    @SuppressWarnings("UnusedReturnValue")
    public static String getAppNameValue() throws RegistryCanNotReadInfoException {
        if (settings.getProperty(KEY_APP_NAME) == null) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                                                                   REGISTRY_APP_PATH,
                                                                   KEY_APP_NAME);
                settings.setProperty(KEY_APP_NAME, value);
                return value;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException("Can not read Installed Location value : " +
                                                                  "HKLM\\" + REGISTRY_APP_PATH + "" + KEY_APP_NAME, e);
            }
        } else return settings.getProperty(KEY_APP_NAME);

    }

    public static void setAppNameValue() throws RegistryCanNotWriteInfoException {
        RegistryManager.createRegistryEntry(KEY_APP_NAME, ApplicationConstants.APP_NAME);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static String getURLUpdateValue() throws RegistryCanNotReadInfoException {

        if (settings.getProperty(KEY_URL_UPDATE_LINK) == null) {
            try {
                String value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                                                                   REGISTRY_APP_PATH,
                                                                   KEY_URL_UPDATE_LINK);
                settings.setProperty(KEY_URL_UPDATE_LINK, value);
                return value;
            } catch (Win32Exception e) {
                throw new RegistryCanNotReadInfoException(
                        "Can not read Installed Location value : " +
                                "HKLM\\" + REGISTRY_APP_PATH + "" + KEY_URL_UPDATE_LINK, e);
            }
        } else return settings.getProperty(KEY_URL_UPDATE_LINK);

    }

    public static void setURLUpdateValue() throws RegistryCanNotWriteInfoException {
        RegistryManager.createRegistryEntry(KEY_URL_UPDATE_LINK, ApplicationConstants.UPDATE_WEB_URL);
    }

    public static void createRegistryEntry(String valueName, String value) throws RegistryCanNotWriteInfoException {
        try {
            settings.setProperty(valueName, value);
            Advapi32Util.registrySetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, valueName, value);
        } catch (Win32Exception e) {
            throw new RegistryCanNotWriteInfoException("Can not create entry at: "
                                                               + APP_ROOT_HKEY + "\\" + REGISTRY_APP_PATH + valueName
                                                               + " With value [" + value + "]", e);
        }
    }

    /**
     * Sets default Settings if can not use registry;
     * It will help to prevent app from crash (if install location is not currupt);
     */
    public static void setDefaultSettings() {
        settings.setProperty(KEY_CURRENT_VERSION, ApplicationConstants.APP_VERSION);
        settings.setProperty(KEY_AUTO_UPDATE, Boolean.toString(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE));
        settings.setProperty(KEY_APP_NAME, ApplicationConstants.APP_NAME);
        settings.setProperty(KEY_URL_UPDATE_LINK, ApplicationConstants.UPDATE_WEB_URL);
    }
}



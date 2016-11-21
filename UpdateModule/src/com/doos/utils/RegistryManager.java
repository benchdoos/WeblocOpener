package com.doos.utils;

//import com.sun.deploy.util.WinRegistry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;

/**
 * Created by Eugene Zrazhevsky on 19.11.2016.
 */
public class RegistryManager {
    public static final String KEY_INSTALL_LOCATION = "InstallLocation";
    public static final String KEY_CURRENT_VERSION = "CurrentVersion";
    public static final String KEY_AUTO_UPDATE = "AutoUpdateEnabled";
    static final WinReg.HKEY APP_ROOT_HKEY = HKEY_CURRENT_USER;
    private final static String REGISTRY_APP_PATH = "SOFTWARE\\" + ApplicationConstants.APP_NAME + "\\";

    public static String getInstallLocationValue() throws Win32Exception { //TODO create normal exception handlers
        String value = null;
        value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                REGISTRY_APP_PATH,
                KEY_INSTALL_LOCATION);

        return value;
    }

    public static String getAppVersionValue() {

        return Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                REGISTRY_APP_PATH,
                KEY_CURRENT_VERSION);
    }

    public static void setAppVersionValue(String version) {
        createRegistryEntry(KEY_CURRENT_VERSION, version);
    }

    public static boolean isAutoUpdateActive() {
        boolean result = true;
        String value = null;

        try {
            value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, KEY_AUTO_UPDATE);
            result = Boolean.parseBoolean(value);

        } catch (Win32Exception e) {
            //fixes
            setAutoUpdateActive(true);
        }


        return result;
    }

    public static void setAutoUpdateActive(boolean autoUpdateActive) {

        Advapi32Util.registrySetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, KEY_AUTO_UPDATE,
                Boolean.toString(autoUpdateActive));
    }

    public static void createRegistryEntry(String valueName, String value) {
        Advapi32Util.registrySetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, valueName, value);
    }
}



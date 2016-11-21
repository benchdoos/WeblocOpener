package com.doos.utils.registry;

//import com.sun.deploy.util.WinRegistry;

import com.doos.utils.ApplicationConstants;
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
    private static final WinReg.HKEY APP_ROOT_HKEY = HKEY_CURRENT_USER;
    private final static String REGISTRY_APP_PATH = "SOFTWARE\\" + ApplicationConstants.APP_NAME + "\\";

    public static String getInstallLocationValue() throws RegistryCanNotReadInfoException {
        String value;
        try {
            value = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                    REGISTRY_APP_PATH,
                    KEY_INSTALL_LOCATION);
        } catch (Win32Exception e) {
            throw new RegistryCanNotReadInfoException("Can not read Installed Location value", e);
        }

        return value;
    }

    public static String getAppVersionValue() throws RegistryCanNotReadInfoException {
        String result = null;
        try {
            result = Advapi32Util.registryGetStringValue(APP_ROOT_HKEY,
                    REGISTRY_APP_PATH,
                    KEY_CURRENT_VERSION);
        } catch (Win32Exception e) {
            throw new RegistryCanNotReadInfoException("Can not get app version value", e);
        }

        return result;
    }

    public static void setAppVersionValue(String version) throws RegistryCanNotWriteInfoException {
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
            try {
                setAutoUpdateActive(true);
            } catch (RegistryCanNotWriteInfoException ignore) {/*NOP*/}
        }


        return result;
    }

    public static void setAutoUpdateActive(boolean autoUpdateActive) throws RegistryCanNotWriteInfoException {

        try {
            Advapi32Util.registrySetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, KEY_AUTO_UPDATE,
                    Boolean.toString(autoUpdateActive));
        } catch (Win32Exception e) {
            throw new RegistryCanNotWriteInfoException("Can not set autoUpdateActive value", e);
        }
    }

    public static void createRegistryEntry(String valueName, String value) throws RegistryCanNotWriteInfoException {
        try {
            Advapi32Util.registrySetStringValue(APP_ROOT_HKEY, REGISTRY_APP_PATH, valueName, value);
        } catch (Win32Exception e) {
            throw new RegistryCanNotWriteInfoException("Can not create entry at: "
                    + APP_ROOT_HKEY + "\\" + REGISTRY_APP_PATH + valueName + " With value [" + value + "]", e);
        }
    }
}



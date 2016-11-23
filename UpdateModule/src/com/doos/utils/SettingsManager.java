package com.doos.utils;

import com.doos.utils.registry.RegistryCanNotReadInfoException;
import com.doos.utils.registry.RegistryCanNotWriteInfoException;
import com.doos.utils.registry.RegistryException;
import com.doos.utils.registry.RegistryManager;
import com.doos.utils.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.utils.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Created by Eugene Zrazhevsky on 20.11.2016.
 */
public class SettingsManager {
    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH =
            "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;

    public static void updateInfo(Properties properties) {
        try {
            final String property = properties.getProperty(RegistryManager.KEY_AUTO_UPDATE);
            if (property != null) {
                RegistryManager.setAutoUpdateActive(Boolean.parseBoolean(property));
            } else {
                RegistryManager.setAutoUpdateActive(true);
            }
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        } catch (RegistryCanNotWriteInfoException e) {
            e.printStackTrace();
        }
    }

    public static Properties loadInfo() throws RegistryCanNotReadInfoException, RegistryCanNotWriteInfoException {
        Properties result = new Properties();
        result.setProperty(RegistryManager.KEY_AUTO_UPDATE, Boolean.toString(RegistryManager.isAutoUpdateActive()));

        result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());

        //Fixes registry after update (if needed)
        if (!RegistryManager.getAppVersionValue().equals(ApplicationConstants.APP_VERSION)) {
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        }
        result.setProperty(RegistryManager.KEY_INSTALL_LOCATION, RegistryManager.getInstallLocationValue());
        return result;
    }

    public static Properties fixRegistry()
            throws RegistryException, FileNotFoundException {
        System.out.println("[REGISTRY FIXER] Trying to check and fix registry values.");
        Properties result = new Properties();
        try {
            result.setProperty(RegistryManager.KEY_AUTO_UPDATE, Boolean.toString(RegistryManager.isAutoUpdateActive()));

        } catch (RegistryCanNotReadInfoException e) {
            try {

                RegistryManager.setAutoUpdateActive(ApplicationConstants.APP_AUTO_UPDATE_DEFAULT_VALUE);
                System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_AUTO_UPDATE);
            } catch (RegistryCanNotWriteInfoException e1) {
                throw new RegistryFixerAutoUpdateKeyFailException("", e);
            }
        } finally {
            try {
                result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());
            } catch (RegistryCanNotReadInfoException e) {
                try {
                    RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
                    System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_CURRENT_VERSION);
                } catch (RegistryCanNotWriteInfoException e1) {
                    throw new RegistryFixerAppVersionKeyFailException("", e);
                }
            } finally {
                try {
                    result.setProperty(RegistryManager.KEY_INSTALL_LOCATION, RegistryManager.getInstallLocationValue());
                } catch (RegistryCanNotReadInfoException e) {
                    String path;
                    try {
                        path = Advapi32Util
                                .registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
                                                        DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH,
                                                        RegistryManager.KEY_INSTALL_LOCATION);

                    } catch (Win32Exception e1) {
                        throw new RegistryCanNotReadInfoException(
                                "Can not read value from Windows UnInstaller: " + "HKLM\\" +
                                        DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH, e);
                    }

                    if (!path.isEmpty()) {
                        if (new File(path).exists() && new File(path).isDirectory()) {
                            RegistryManager.setInstallLocationValue(path);
                        } else {
                            throw new FileNotFoundException(
                                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                                            "FOLDER]: " + path);
                        }
                    } else {
                        if (new File(path).exists() && new File(path).isDirectory() && new File(path).getName().equals
                                (ApplicationConstants.APP_NAME)) {
                            RegistryManager.setInstallLocationValue(path);
                        } else {
                            throw new FileNotFoundException(
                                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                                            "FOLDER]: [" + path + "]");
                        }
                    }
                }
            }
        }

        return result;
    }
}

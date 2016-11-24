package com.doos.settings_manager.registry.fixer;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryCanNotWriteInfoException;
import com.doos.settings_manager.registry.RegistryManager;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Created by Eugene Zrazhevsky on 24.11.2016.
 */
public class RegistryFixer {
    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH =
            "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;

    public static Properties fixRegistry()
            throws FileNotFoundException, RegistryFixerAutoUpdateKeyFailException, RegistryFixerAppVersionKeyFailException, RegistryFixerInstallPathKeyFailException {
        System.out.println(
                "[REGISTRY FIXER] Trying to check and fix registry values.");
        Properties result = new Properties();
        result = fixAutoUpdateValue(result);
        result = fixAppVersionValue(result);
        result = fixInstallLocationValue(result);
        fixAppNameValue();
        fixUpdateUrlValue();
        /*Add here sub-directories if needed*/
        return result;

    }

    private static void fixAppNameValue() {
        try {
            RegistryManager.getAppNameValue();
        } catch (RegistryCanNotReadInfoException e1) {
            try {
                RegistryManager.setAppNameValue(ApplicationConstants.APP_NAME);
                System.out.println(
                        "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_APP_NAME);
            } catch (RegistryCanNotWriteInfoException ignore) {/*NOP*/}
        }
    }

    private static void fixUpdateUrlValue() {
        try {
            RegistryManager.getURLUpdateValue();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setURLUpdateValue(ApplicationConstants.UPDATE_WEB_URL);
                System.out.println(
                        "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_URL_UPDATE_LINK);
            } catch (RegistryCanNotWriteInfoException e2) {/*NOP*/}
        }
    }

    private static Properties fixInstallLocationValue(Properties result)
            throws FileNotFoundException, RegistryFixerInstallPathKeyFailException {
        try {
            result.setProperty(RegistryManager.KEY_INSTALL_LOCATION, RegistryManager.getInstallLocationValue());
        } catch (RegistryCanNotReadInfoException e) {
            String path;
            try {
                path = getLocationFromInstallerValue(e);

                if (!path.isEmpty()) {
                    setAppInstallLocationToDefault(result, path);
                } else {
                    setAppInstallLocationToUserRoot(result, path);
                }
            } catch (RegistryCanNotReadInfoException | RegistryCanNotWriteInfoException e1) {
                throw new RegistryFixerInstallPathKeyFailException();
            }
        }
        return result;
    }

    private static void setAppInstallLocationToUserRoot(Properties result, String path)
            throws RegistryCanNotWriteInfoException, RegistryCanNotReadInfoException, FileNotFoundException {
        if (new File(path).exists() && new File(path).isDirectory() && new File(path).getName().equals
                (ApplicationConstants.APP_NAME)) {
            RegistryManager.setInstallLocationValue(path);
            result.setProperty(RegistryManager.KEY_INSTALL_LOCATION,
                               RegistryManager.getInstallLocationValue());
            System.out.println(
                    "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);

        } else {
            throw new FileNotFoundException(
                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                            "FOLDER]: [" + path + "]");
        }
    }

    private static void setAppInstallLocationToDefault(Properties result, String path)
            throws RegistryCanNotWriteInfoException, RegistryCanNotReadInfoException, FileNotFoundException {
        if (new File(path).exists() && new File(path).isDirectory()) {
            RegistryManager.setInstallLocationValue(path);
            result.setProperty(RegistryManager.KEY_INSTALL_LOCATION,
                               RegistryManager.getInstallLocationValue());
            System.out.println(
                    "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);
        } else {
            throw new FileNotFoundException(
                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                            "FOLDER]: " + path);
        }
    }

    private static String getLocationFromInstallerValue(RegistryCanNotReadInfoException e)
            throws RegistryCanNotReadInfoException {
        String path;
        try {
            path = WinReg.HKEY_LOCAL_MACHINE + "\\" +
                    DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH + "\\" +
                    RegistryManager.KEY_INSTALL_LOCATION;
            System.out.println("PPPPPP:" + path);
            path = Advapi32Util
                    .registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
                                            DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH,
                                            RegistryManager.KEY_INSTALL_LOCATION);

        } catch (Exception e1) {
            throw new RegistryCanNotReadInfoException(
                    "Can not read value from Windows UnInstaller: " + "HKLM\\" +
                            DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH, e);
        }
        return path;
    }

    private static Properties fixAppVersionValue(Properties result)
            throws RegistryFixerAppVersionKeyFailException {
        try {
            result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
                result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());
                System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_CURRENT_VERSION);
            } catch (RegistryCanNotReadInfoException | RegistryCanNotWriteInfoException e1) {
                throw new RegistryFixerAppVersionKeyFailException("", e);
            }
        }

        return result;
    }

    private static Properties fixAutoUpdateValue(Properties result) throws RegistryFixerAutoUpdateKeyFailException {
        try {
            try {
                result.setProperty(RegistryManager.KEY_AUTO_UPDATE,
                                   Boolean.toString(RegistryManager.isAutoUpdateActive()));

            } catch (RegistryCanNotReadInfoException e) {
                try {

                    RegistryManager.setAutoUpdateActive(ApplicationConstants.APP_AUTO_UPDATE_DEFAULT_VALUE);
                    System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_AUTO_UPDATE);
                    result.setProperty(RegistryManager.KEY_AUTO_UPDATE,
                                       Boolean.toString(RegistryManager.isAutoUpdateActive()));
                } catch (RegistryCanNotWriteInfoException e1) {
                    throw new RegistryFixerAutoUpdateKeyFailException("", e);
                }
            }
        } catch (RegistryCanNotReadInfoException | RegistryFixerAutoUpdateKeyFailException e) {
            throw new RegistryFixerAutoUpdateKeyFailException("", e);
        }
        return result;
    }
}

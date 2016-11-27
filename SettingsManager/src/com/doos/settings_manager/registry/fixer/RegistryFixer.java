package com.doos.settings_manager.registry.fixer;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryCanNotWriteInfoException;
import com.doos.settings_manager.registry.RegistryManager;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Eugene Zrazhevsky on 24.11.2016.
 */
public class RegistryFixer { //TODO Enable logging
    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH =
            "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;

    public static void fixRegistry()
            throws FileNotFoundException, RegistryFixerAutoUpdateKeyFailException,
            RegistryFixerAppVersionKeyFailException, RegistryFixerInstallPathKeyFailException {
        System.out.println(
                "[REGISTRY FIXER] Trying to check and fix registry values.");
        fixAutoUpdateValue();
        fixAppVersionValue();
        fixInstallLocationValue();
        fixAppNameValue();
        fixUpdateUrlValue();
        /*TODO Add here sub-directories if needed*/
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

    private static void fixInstallLocationValue()
            throws FileNotFoundException, RegistryFixerInstallPathKeyFailException {
        try {
            RegistryManager.getInstallLocationValue();
        } catch (RegistryCanNotReadInfoException e) {
            String path;
            try {
                path = getLocationFromInstallerValue();

                if (!path.isEmpty()) {
                    setAppInstallLocationToDefault(path);
                } else {
                    setAppInstallLocationToUserRoot(path);
                }
            } catch (RegistryCanNotReadInfoException | RegistryCanNotWriteInfoException e1) {
                throw new RegistryFixerInstallPathKeyFailException();
            }
        }
    }

    private static void setAppInstallLocationToUserRoot(String path)
            throws RegistryCanNotWriteInfoException, RegistryCanNotReadInfoException, FileNotFoundException {
        if (new File(path).exists() && new File(path).isDirectory() && new File(path).getName().equals
                (ApplicationConstants.APP_NAME)) {
            RegistryManager.setInstallLocationValue(path);
            System.out.println(
                    "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);
        } else {
            throw new FileNotFoundException(
                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                            "FOLDER]: [" + path + "]");
        }
    }

    private static void setAppInstallLocationToDefault(String path)
            throws RegistryCanNotWriteInfoException, RegistryCanNotReadInfoException, FileNotFoundException {
        if (new File(path).exists() && new File(path).isDirectory()) {
            RegistryManager.setInstallLocationValue(path);
            System.out.println(
                    "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);
        } else {
            throw new FileNotFoundException(
                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                            "FOLDER]: " + path);
        }
    }

    private static String getLocationFromInstallerValue()
            throws RegistryCanNotReadInfoException {
        String path;
        try {
            path = WinReg.HKEY_LOCAL_MACHINE + "\\" +
                    DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH + "\\" +
                    RegistryManager.KEY_INSTALL_LOCATION;
            System.out.println("Default installer value:" + path);
            path = Advapi32Util
                    .registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
                                            DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH,
                                            RegistryManager.KEY_INSTALL_LOCATION);

        } catch (Exception e) {
            throw new RegistryCanNotReadInfoException(
                    "Can not read value from Windows UnInstaller: " + "HKLM\\" +
                            DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH, e);
        }
        return path;
    }

    private static void fixAppVersionValue()
            throws RegistryFixerAppVersionKeyFailException {
        try {
            RegistryManager.getAppVersionValue();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
                System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_CURRENT_VERSION);
            } catch (RegistryCanNotWriteInfoException e1) {
                throw new RegistryFixerAppVersionKeyFailException("", e);
            }
        }
    }

    private static void fixAutoUpdateValue() throws RegistryFixerAutoUpdateKeyFailException {
        try {
            RegistryManager.isAutoUpdateActive();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAutoUpdateActive(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
                System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_AUTO_UPDATE);
            } catch (RegistryCanNotWriteInfoException e1) {
                throw new RegistryFixerAutoUpdateKeyFailException("", e);
            }
        }

    }
}

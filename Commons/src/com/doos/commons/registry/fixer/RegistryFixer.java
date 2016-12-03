package com.doos.commons.registry.fixer;

import com.doos.commons.ApplicationConstants;
import com.doos.commons.registry.RegistryCanNotReadInfoException;
import com.doos.commons.registry.RegistryCanNotWriteInfoException;
import com.doos.commons.registry.RegistryManager;
import com.doos.commons.utils.system.SystemUtils;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Eugene Zrazhevsky on 24.11.2016.
 */
public class RegistryFixer { //TODO Enable logging
    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH_64 =
            "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;
    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH_32 =
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;

    public static void fixRegistry()
            throws FileNotFoundException, RegistryFixerAutoUpdateKeyFailException,
            RegistryFixerAppVersionKeyFailException, RegistryFixerInstallPathKeyFailException {
        System.out.println("[REGISTRY FIXER] Trying to check and fix registry values.");
        fixRootRegistryUnit();
        fixAutoUpdateValue();
        fixAppVersionValue();
        fixInstallLocationValue();
        fixAppNameValue();
        fixUpdateUrlValue();
        /*TODO Add here sub-directories if needed*/
    }

    private static void fixRootRegistryUnit() {
        System.out.println("[REGISTRY FIXER] Trying to fix Root app registry path");
        try {
            RegistryManager.createRootRegistryFolder();
            System.out.println("[REGISTRY FIXER]Successfully Fixed Root app registry path");
        } catch (RegistryCanNotWriteInfoException e) {
            System.out.println("[REGISTRY FIXER] Failed to fix Root app registry path");
            e.printStackTrace();
        }

    }

    private static void fixAppNameValue() {
        try {
            System.out.println("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_APP_NAME);
            RegistryManager.getAppNameValue();
        } catch (RegistryCanNotReadInfoException e1) {
            try {
                RegistryManager.setAppNameValue();
                System.out.println(
                        "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_APP_NAME);
            } catch (RegistryCanNotWriteInfoException ignore) {
                System.out.println("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_APP_NAME);
            }
        }
    }

    private static void fixUpdateUrlValue() {
        try {
            System.out.println("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_URL_UPDATE_LINK);
            RegistryManager.getURLUpdateValue();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setURLUpdateValue();
                System.out.println(
                        "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_URL_UPDATE_LINK);
            } catch (RegistryCanNotWriteInfoException e2) {
                e2.printStackTrace();
                System.out.println("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_URL_UPDATE_LINK);
            }
        }
    }

    private static void fixInstallLocationValue()
            throws FileNotFoundException, RegistryFixerInstallPathKeyFailException {
        try {
            System.out.println("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_INSTALL_LOCATION);
            RegistryManager.getInstallLocationValue();
        } catch (RegistryCanNotReadInfoException e) {
            String path;
            try {
                path = getLocationFromInstallerValue();
                System.out.println("Probably path is: '" + path + "'");
                if (!path.isEmpty()) {
                    setAppInstallLocationToDefault(path);
                } else {
                    setAppInstallLocationToUserRoot(path);
                }
            } catch (RegistryCanNotReadInfoException | RegistryCanNotWriteInfoException e1) {
                System.out.println("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_INSTALL_LOCATION);
                throw new RegistryFixerInstallPathKeyFailException();
            }
        }
    }

    private static void setAppInstallLocationToUserRoot(String path)
            throws RegistryCanNotWriteInfoException, FileNotFoundException {
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
            throws RegistryCanNotWriteInfoException, FileNotFoundException {
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
        String pathValue;
        pathValue = getUnistallLocationForCurrentSystemArch();

        try {
            path = WinReg.HKEY_LOCAL_MACHINE + "\\" +
                    pathValue + "\\" +
                    RegistryManager.KEY_INSTALL_LOCATION;
            System.out.println("Default installer value:" + path);
            path = Advapi32Util
                    .registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
                                            pathValue,
                                            RegistryManager.KEY_INSTALL_LOCATION);
            System.out.println("Default installer path: '" + path + "'");

        } catch (Exception e) {
            throw new RegistryCanNotReadInfoException(
                    "Can not read value from Windows UnInstaller: " + "HKLM\\" +
                            pathValue, e);
        }
        return path;
    }

    private static String getUnistallLocationForCurrentSystemArch() {
        String pathValue;
        if (SystemUtils.getRealSystemArch().equals("64")) {
            pathValue = DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH_64;
        } else {
            pathValue = DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH_32;
        }
        return pathValue;
    }

    private static void fixAppVersionValue()
            throws RegistryFixerAppVersionKeyFailException {
        try {
            System.out.println("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_CURRENT_VERSION);
            RegistryManager.getAppVersionValue();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
                System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_CURRENT_VERSION);
            } catch (RegistryCanNotWriteInfoException e1) {
                System.out.println("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_CURRENT_VERSION);
                throw new RegistryFixerAppVersionKeyFailException("", e);
            }
        }
    }

    private static void fixAutoUpdateValue() throws RegistryFixerAutoUpdateKeyFailException {
        try {
            System.out.println("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_AUTO_UPDATE);

            RegistryManager.isAutoUpdateActive();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAutoUpdateActive(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
                System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_AUTO_UPDATE);
            } catch (RegistryCanNotWriteInfoException e1) {
                System.out.println("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_AUTO_UPDATE);
                throw new RegistryFixerAutoUpdateKeyFailException("", e);
            }
        }

    }
}

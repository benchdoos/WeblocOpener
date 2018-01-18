/*
 * Copyright 2018 Eugeny Zrazhevsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.benchdoos.weblocopener.commons.registry.fixer;

import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.registry.RegistryCanNotReadInfoException;
import com.github.benchdoos.weblocopener.commons.registry.RegistryCanNotWriteInfoException;
import com.github.benchdoos.weblocopener.commons.registry.RegistryException;
import com.github.benchdoos.weblocopener.commons.registry.RegistryManager;
import com.github.benchdoos.weblocopener.commons.utils.system.SystemUtils;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;

import static com.github.benchdoos.weblocopener.commons.utils.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 24.11.2016.
 */
public class RegistryFixer { //TODO Enable logging
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH_64 =
            "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;
    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH_32 =
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;

    public static void fixRegistry()
            throws FileNotFoundException, RegistryFixerAutoUpdateKeyFailException,
            RegistryFixerAppVersionKeyFailException, RegistryFixerInstallPathKeyFailException {

        try {
            checkRegistry();
        } catch (RegistryException e) {
            log.info("Something is wrong with registry.", e);
            fixRegistryAnyway();
        }
    }

    public static void fixRegistryAnyway() throws RegistryFixerAutoUpdateKeyFailException,
            RegistryFixerAppVersionKeyFailException, FileNotFoundException, RegistryFixerInstallPathKeyFailException {
        log.info("[REGISTRY FIXER] Trying to check and fix registry values. [REGISTRY FIXER]");
        RegistryManager.repealSettings();

        fixRootRegistryUnit();
        fixAutoUpdateValue();
        fixAppVersionValue();
        fixInstallLocationValue();
        fixAppNameValue();
        fixUpdateUrlValue();

        fixCapabilitiesRegistryUnit();
        fixFileAssociationsRegistryUnit();
        fixApplicationDescriptionValue();
        fixFileAssociationsValues();
    }

    private static void fixRootRegistryUnit() {
        log.info("[REGISTRY FIXER] Trying to fix Root app registry path");
        try {
            RegistryManager.createRootRegistryFolder(RegistryManager.REGISTRY_APP_PATH);
            log.info("[REGISTRY FIXER] Successfully Fixed Root app registry path");
        } catch (RegistryCanNotWriteInfoException e) {
            log.warn("[REGISTRY FIXER] Failed to fix Root app registry path", e);
        }

    }

    private static void fixAppNameValue() {
        try {
            log.info("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_APP_NAME);
            RegistryManager.getAppNameValue();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAppNameValue();
                log.info("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_APP_NAME);
            } catch (RegistryCanNotWriteInfoException e1) {
                log.warn("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_APP_NAME, e1);
            }
        }
    }

    private static void fixUpdateUrlValue() {
        try {
            log.info("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_URL_UPDATE_LINK);
            RegistryManager.getURLUpdateValue();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setURLUpdateValue();
                log.info(
                        "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_URL_UPDATE_LINK);
            } catch (RegistryCanNotWriteInfoException e1) {
                log.warn("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_URL_UPDATE_LINK, e);
            }
        }
    }

    private static void fixInstallLocationValue()
            throws FileNotFoundException, RegistryFixerInstallPathKeyFailException {
        try {
            log.info("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_INSTALL_LOCATION);
            RegistryManager.getInstallLocationValue();
        } catch (RegistryCanNotReadInfoException e) {
            String path;
            try {
                path = getLocationFromInstallerValue();
                log.info("Probably path is: '" + path + "'");
                if (!path.isEmpty()) {
                    setAppInstallLocationToDefault(path);
                } else {
                    setAppInstallLocationToUserRoot(path);
                }
            } catch (RegistryCanNotReadInfoException | RegistryCanNotWriteInfoException e1) {
                log.warn("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_INSTALL_LOCATION, e1);
                throw new RegistryFixerInstallPathKeyFailException();
            }
        }
    }

    private static void setAppInstallLocationToUserRoot(String path)
            throws RegistryCanNotWriteInfoException, FileNotFoundException {
        if (new File(path).exists() && new File(path).isDirectory() && new File(path).getName().equals
                (ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME)) {
            RegistryManager.setInstallLocationValue(path);
            log.info("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);
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
            log.info("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);
        } else {
            String message = "It is not a directory or does not exist [WINDOWS UNINSTALL FOLDER]: " + path;
            log.warn(message);
            throw new FileNotFoundException(message);
        }
    }

    private static String getLocationFromInstallerValue()
            throws RegistryCanNotReadInfoException {
        String path;
        String pathValue;
        pathValue = getUninstallLocationForCurrentSystemArch();

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
            final String message = "Can not read value from Windows UnInstaller: " + "HKLM\\" +
                    pathValue;
            log.warn(message, e);
            throw new RegistryCanNotReadInfoException(
                    message, e);
        }
        return path;
    }

    private static String getUninstallLocationForCurrentSystemArch() {
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
            log.info("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_CURRENT_VERSION);
            RegistryManager.getAppVersionValue();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
                log.info("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_CURRENT_VERSION);
            } catch (RegistryCanNotWriteInfoException e1) {
                log.warn("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_CURRENT_VERSION, e);
                throw new RegistryFixerAppVersionKeyFailException("", e);
            }
        }
    }

    private static void fixAutoUpdateValue() throws RegistryFixerAutoUpdateKeyFailException {
        try {
            log.info("[REGISTRY FIXER] Trying to fix " + RegistryManager.KEY_AUTO_UPDATE);

            RegistryManager.isAutoUpdateActive();
        } catch (RegistryCanNotReadInfoException e) {
            try {
                RegistryManager.setAutoUpdateActive(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
                log.info("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_AUTO_UPDATE);
            } catch (RegistryCanNotWriteInfoException e1) {
                log.warn("[REGISTRY FIXER] Failed to fix " + RegistryManager.KEY_AUTO_UPDATE, e);
                throw new RegistryFixerAutoUpdateKeyFailException("", e);
            }
        }

    }

    private static void fixCapabilitiesRegistryUnit() {
        log.info("[REGISTRY FIXER] Trying to fix Capabilities app registry path");
        try {
            RegistryManager.createRootRegistryFolder(RegistryManager.REGISTRY_APP_PATH + "Capabilities\\");
            log.info("[REGISTRY FIXER] Successfully Fixed Capabilities app registry path");
        } catch (RegistryCanNotWriteInfoException e) {
            log.warn("[REGISTRY FIXER] Failed to fix Capabilities app registry path", e);
        }
    }

    private static void fixFileAssociationsValues() {
        log.info("[REGISTRY FIXER] Trying to fix value: ApplicationDescription");
        try {
            RegistryManager.createRegistryEntry(RegistryManager.REGISTRY_APP_PATH +
                            "Capabilities\\", "ApplicationDescription",
                    "Open, edit and create .webloc links on Windows");
            log.info("[REGISTRY FIXER] Successfully Fixed value: ApplicationDescription");
        } catch (RegistryCanNotWriteInfoException e) {
            log.warn("[REGISTRY FIXER] Failed to fix value: ApplicationDescription", e);
        }
    }

    private static void fixApplicationDescriptionValue() {
        log.info("[REGISTRY FIXER] Trying to fix value: ApplicationDescription");
        try {
            RegistryManager.createRegistryEntry(RegistryManager.REGISTRY_APP_PATH + "Capabilities\\FileAssociations\\",
                    ".webloc", "Webloc link");
            log.info("[REGISTRY FIXER] Successfully Fixed value: ApplicationDescription");
        } catch (RegistryCanNotWriteInfoException e) {
            log.warn("[REGISTRY FIXER] Failed to fix value: ApplicationDescription", e);
        }
    }

    private static void fixFileAssociationsRegistryUnit() {
        log.info("[REGISTRY FIXER] Trying to fix FileAssociations app registry path");
        try {
            RegistryManager.createRootRegistryFolder(RegistryManager.REGISTRY_APP_PATH +
                    "Capabilities\\FileAssociations\\");
            log.info("[REGISTRY FIXER] Successfully Fixed FileAssociations app registry path");
        } catch (RegistryCanNotWriteInfoException e) {
            log.warn("[REGISTRY FIXER] Failed to fix FileAssociations app registry path", e);
        }
    }


    private static void checkRegistry() throws RegistryException {
        log.info("Checking important registry info.");
        RegistryManager.getAppNameValue();
        String version = RegistryManager.getAppVersionValue();
        if (!version.equals(ApplicationConstants.APP_VERSION)) {
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        }
        RegistryManager.getInstallLocationValue();
        RegistryManager.getURLUpdateValue();
        log.info("Registry is alright.");
    }
}

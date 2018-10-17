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

package com.github.benchdoos.weblocopener.updater.core;

import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.registry.RegistryCanNotReadInfoException;
import com.github.benchdoos.weblocopener.commons.registry.RegistryManager;
import com.github.benchdoos.weblocopener.commons.utils.Internal;
import com.github.benchdoos.weblocopener.commons.utils.Logging;
import com.github.benchdoos.weblocopener.commons.utils.UserUtils;
import com.github.benchdoos.weblocopener.commons.utils.system.SystemUtils;
import com.github.benchdoos.weblocopener.commons.utils.system.UnsupportedOsSystemException;
import com.github.benchdoos.weblocopener.commons.utils.system.UnsupportedSystemVersionException;
import com.github.benchdoos.weblocopener.updater.gui.UpdateDialog;
import com.github.benchdoos.weblocopener.updater.nongui.NonGuiUpdater;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class Main {
    public static Mode mode = Mode.NORMAL;
    private static Logger log;

    public static void main(String[] args) {
        try {
            new Logging(ApplicationConstants.UPDATER_APPLICATION_NAME);
            log = Logger.getLogger(Logging.getCurrentClassName());

            SystemUtils.checkIfSystemIsSupported();

            enableLookAndFeel();

            tryLoadProperties();

            System.out.println("Updater args: " + Arrays.toString(args));

            manageArguments(args);
        } catch (UnsupportedOsSystemException | UnsupportedSystemVersionException e) {
            UserUtils.showErrorMessageToUser(null, "Error", e.getMessage());
        }
    }

    private static void manageArguments(String[] args) {
        if (args.length > 0) {

            switch (args[0]) {
                case ApplicationConstants.UPDATE_SILENT_ARGUMENT:
                    mode = Mode.SILENT;
                    boolean isAutoUpdate = ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE;

                    isAutoUpdate = RegistryManager.isAutoUpdateActive();

                    log.debug(RegistryManager.KEY_AUTO_UPDATE + " : " + isAutoUpdate);
                    if (isAutoUpdate) {
                        new NonGuiUpdater();
                    }
                    break;
                case ApplicationConstants.UPDATE_DELETE_TEMP_FILE_ARGUMENT:
                    mode = Mode.AFTER_UPDATE;
                    new File(ApplicationConstants.UPDATE_PATH_FILE + "Updater_.jar").delete();
                    File[] files = new File(ApplicationConstants.UPDATE_PATH_FILE).listFiles();
                    if (files != null) {
                        for (File current : files) {
                            if (current.isFile() && current.getName().toLowerCase().contains(
                                    ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME.toLowerCase()) &&
                                    current.getName().toLowerCase().contains("exe")) {
                                current.delete();
                            }
                        }
                    }

                    new File(ApplicationConstants.DEFAULT_LIST_LOCATION).delete();
                    //createUpdateDialog();
                    break;
                default:
                    mode = Mode.ERROR;
                    String message = "No such argument: [" + args[0] + "]";
                    log.warn(message);
                    UserUtils.showWarningMessageToUser(null, null,
                            message);
                    break;
            }
        } else {
            mode = Mode.NORMAL;
            /*-----*/
            try {
                System.out.println("Installation folder: " + RegistryManager.getInstallLocationValue());
            } catch (RegistryCanNotReadInfoException ignore) {/*NOP*/}
            /*-----*/
            initUpdateJar();
        }
    }

    private static void tryLoadProperties() {
       /* try {
            RegistryFixer.fixRegistry();
        } catch (RegistryFixerAutoUpdateKeyFailException | RegistryFixerAppVersionKeyFailException e1) {
            log.warn("Can not fix registry on startup", e1);
            RegistryManager.setDefaultSettings();
        } catch (RegistryFixerInstallPathKeyFailException | FileNotFoundException e1) {
            log.warn("Can not fix registry on startup", e1);
            UserUtils.showErrorMessageToUser(null, "Can not fix registry",
                    "Registry application data is corrupt. " +
                            "Please re-install the " + "application.");
            System.exit(-1);
        }*/
    }

    /**
     * Finds out, where app is located.
     */
    public static void initUpdateJar() {
        final File JAR_FILE = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String property = null;
        try {
            property = RegistryManager.getInstallLocationValue();
        } catch (RegistryCanNotReadInfoException e) {
            e.printStackTrace();
        }
        File JAR_FILE_DEFAULT_LOCATION = getFileLocation(JAR_FILE, property);

        log.info("Jar: " + JAR_FILE.getAbsolutePath() + " def: " + JAR_FILE_DEFAULT_LOCATION.getAbsolutePath());

        runUpdater(JAR_FILE, JAR_FILE_DEFAULT_LOCATION);
    }

    /**
     * Creates a copy of jar file in other location and runs it.
     */
    private static void runUpdater(File JAR_FILE, File JAR_FILE_DEFAULT_LOCATION) {
        if (JAR_FILE.getAbsolutePath().replace("%20", " ").equals(
                JAR_FILE_DEFAULT_LOCATION.getAbsolutePath().replace("%20", " "))) {
            try {
                log.info(
                        "Creating itself at: " + new File(ApplicationConstants.UPDATE_PATH_FILE + "Updater_.jar").getAbsolutePath());
                FileUtils.copyFile(new File(JAR_FILE.getAbsolutePath().replace("%20", " ")),
                        new File(ApplicationConstants.UPDATE_PATH_FILE + "Updater_.jar"));
                Runtime.getRuntime().exec("java -jar " + ApplicationConstants.UPDATE_PATH_FILE + "Updater_.jar");
                System.exit(0);
            } catch (IOException e) {
                log.warn(e);
            }
        } else {
            Main.createUpdateDialog();
        }
    }

    private static File getFileLocation(File JAR_FILE, String property) {
        File JAR_FILE_DEFAULT_LOCATION;
        if (property != null) {
            JAR_FILE_DEFAULT_LOCATION = new File(property
                    + File.separator + "Updater.jar");
        } else {
            File file = JAR_FILE.getParentFile();
            log.info("Parent file is: " + file.getAbsolutePath());
            if (file.getName().equals(ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME)) {
                JAR_FILE_DEFAULT_LOCATION = new File(file.getAbsolutePath() + File.separator + "Updater.jar");
            } else {
                String programFilesPath = getProgramFilesPath();
                JAR_FILE_DEFAULT_LOCATION = new File(
                        programFilesPath + "WeblocOpener" + File.separator + "Updater.jar"); //TODO find better solution
            }
        }
        return JAR_FILE_DEFAULT_LOCATION;
    }

    /**
     * Finds out %ProgramFiles% path
     *
     * @return Path to %ProgramFiles% folder
     */
    private static String getProgramFilesPath() {
        String programFilesPath;
        String realArch = SystemUtils.getRealSystemArch();
        if (realArch.equals("64")) {
            programFilesPath = System.getenv("ProgramFiles(X86)");
        } else {
            programFilesPath = System.getenv("ProgramFiles");
        }
        return programFilesPath;
    }

    private static void createUpdateDialog() {
        final UpdateDialog updateDialog = new UpdateDialog();


        updateDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                String str;
                try {
                    str = RegistryManager.getAppVersionValue();
                } catch (RegistryCanNotReadInfoException e1) {
                    str = ApplicationConstants.APP_VERSION;
                }
                if (str == null) {
                    str = ApplicationConstants.APP_VERSION;
                } else if (str.isEmpty()) {
                    str = ApplicationConstants.APP_VERSION;
                }
                if (Internal.versionCompare(str, updateDialog.getAppVersion().getVersion()) == 0) {
                    NonGuiUpdater.tray.remove(NonGuiUpdater.trayIcon);
                    System.exit(0);
                }
                super.windowClosed(e);

            }

        });
        UpdateDialog.updateDialog = updateDialog;

        updateDialog.setVisible(true);
        updateDialog.checkForUpdates();
    }


    private static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
            log.warn("Could not establish UI Look and feel.");
        }
    }

    public enum Mode {NORMAL, SILENT, AFTER_UPDATE, ERROR}
}

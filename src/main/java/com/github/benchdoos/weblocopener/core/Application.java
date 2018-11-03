/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.core;

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.core.constants.ArgumentConstants;
import com.github.benchdoos.weblocopener.core.constants.PathConstants;
import com.github.benchdoos.weblocopener.gui.*;
import com.github.benchdoos.weblocopener.nongui.NonGuiUpdater;
import com.github.benchdoos.weblocopener.registry.RegistryCanNotReadInfoException;
import com.github.benchdoos.weblocopener.registry.RegistryManager;
import com.github.benchdoos.weblocopener.service.Analyzer;
import com.github.benchdoos.weblocopener.service.UrlsProceed;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.Internal;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.UserUtils;
import com.github.benchdoos.weblocopener.utils.browser.BrowserManager;
import com.github.benchdoos.weblocopener.utils.system.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

import static com.github.benchdoos.weblocopener.core.constants.ArgumentConstants.*;
import static java.awt.Frame.MAXIMIZED_HORIZ;

public class Application {
    private static final String CORRECT_CREATION_SYNTAX = "-create <file path> <url>";
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static UPDATE_MODE updateMode = UPDATE_MODE.NORMAL;


    public Application(String[] args) {
        log.info("{} starts in updateMode: {}", ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME, Main.getCurrentMode());
        log.info("{} starts with arguments: {}", ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME, Arrays.toString(args));

        BrowserManager.loadBrowserList();

        CoreUtils.enableLookAndFeel();

        manageArguments(args);

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

    private static String helpText() {
        return OPENER_CREATE_ARGUMENT + "\t[filepath] [link] \tCreates a new .webloc file on [filepath]. " +
                "[filepath] should end with [\\filename.webloc]\n" +
                OPENER_EDIT_ARGUMENT + "\t[filepath] \t\t\tCalls Edit window to edit given .webloc file.\n" +
                OPENER_SETTINGS_ARGUMENT + "\t\t\t\t\tCalls Settings window of WeblocOpener.\n" +
                OPENER_UPDATE_ARGUMENT + "\t\t\t\t\t\tCalls update-tool for WeblocOpener.";
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
     * Manages incoming arguments
     *
     * @param args App start arguments
     */
    private static void manageArguments(String[] args) {
        log.debug("Managing arguments: " + Arrays.toString(args));
        if (args.length > 0) {
            log.info("Got args: " + Arrays.toString(args));
            if (!args[0].isEmpty()) {
                switch (args[0]) {
                    case OPENER_EDIT_ARGUMENT:
                        manageEditArgument(args);
                        break;
                    case OPENER_SETTINGS_ARGUMENT:
                        runSettingsDialog();
                        break;

                    case OPENER_ABOUT_ARGUMENT:
                        new AboutApplicationDialog().setVisible(true);
                        break;

                    case OPENER_CREATE_ARGUMENT:
                        try {
                            manageCreateArgument(args);
                        } catch (Exception e) {
                            log.warn("Can not create .webloc file (" + CORRECT_CREATION_SYNTAX + "): "
                                    + Arrays.toString(args), e);
                        }
                        break;

                    case OPENER_UPDATE_ARGUMENT:
                        SettingsDialog.runUpdater();
                        break;
                    case OPENER_HELP_ARGUMENT_HYPHEN: {
                        System.out.println(helpText());
                        break;
                    }
                    case OPENER_QR_ARGUMENT:
                        if (args.length > 1) {
                            String url = runAnalyzer(args[1]);

                            ShowQrDialog qrDialog = null;
                            try {
                                qrDialog = new ShowQrDialog(url, UrlsProceed.generateQrCode(url));
                                qrDialog.setVisible(true);
                            } catch (Exception e) {
                                log.warn("Can not create a qr-code from url: [" + url + "]", e);
                            }
                        }
                        break;
                    case OPENER_COPY_ARGUMENT:
                        if (args.length > 1) {
                            final String path = args[1];
                            String url = runAnalyzer(path);
                            StringSelection stringSelection = new StringSelection(url);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(stringSelection, null);
                            log.info("Successfully copied url to clipboard from: " + path);
                        }
                        break;

                    case ArgumentConstants.UPDATE_SILENT_ARGUMENT:
                        updateMode = UPDATE_MODE.SILENT;
                        boolean isAutoUpdate = RegistryManager.isAutoUpdateActive();

                        log.debug(RegistryManager.KEY_AUTO_UPDATE + " : " + isAutoUpdate);
                        if (isAutoUpdate) {
                            new NonGuiUpdater();
                        }
                        break;
                    case ArgumentConstants.UPDATE_DELETE_TEMP_FILE_ARGUMENT:
                        updateMode = UPDATE_MODE.AFTER_UPDATE;
                        new File(PathConstants.UPDATE_PATH_FILE + "Updater_.jar").delete();
                        File[] files = new File(PathConstants.UPDATE_PATH_FILE).listFiles();
                        if (files != null) {
                            for (File current : files) {
                                if (current.isFile() && current.getName().toLowerCase().contains(
                                        ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME.toLowerCase()) &&
                                        current.getName().toLowerCase().contains("exe")) {
                                    current.delete();
                                }
                            }
                        }
                        break;
                    default:
                        String url = runAnalyzer(args[0]);
                        UrlsProceed.openUrl(url);
                        UrlsProceed.shutdownLogout();
                        break;
                }
            } else {
                log.warn("Illegal argument at index 0 : Argument is empty!");
            }
        } else {
            log.debug("No arguments found, launching settings");
            runSettingsDialog();
        }
    }

    private static void manageCreateArgument(String[] args) throws Exception {
        String filePath;
        String url;
        if (args.length > 2) {
            filePath = args[1];
            url = args[2];
            if (args.length > 3) {
                log.info("You can create only one link in one file. Creating.");
            }
            UrlsProceed.createWebloc(filePath, new URL(url));
        } else {
            throw new IllegalArgumentException("Not all arguments (" + CORRECT_CREATION_SYNTAX + "):" + Arrays.toString(args));
        }
    }

    /**
     * Manages edit argument, runs edit-updateMode to file in second argument
     *
     * @param args main args
     */
    private static void manageEditArgument(String[] args) {
        if (args.length > 1) {
            runEditDialog(args[1]);
        } else {
            UserUtils.showErrorMessageToUser(null, "Error",
                    "Argument '-edit' should have location path parameter.");
        }
    }

    /**
     * Manages default incorrect argument (or url)
     *
     * @param arg main args
     */
    private static String runAnalyzer(String arg) {
        return new Analyzer(arg).getUrl();
    }

    /**
     * Runs EditDialog
     *
     * @param arg file path
     */
    private static void runEditDialog(String arg) {
        EditDialog dialog = new EditDialog(arg);
        dialog.setVisible(true);
        dialog.setMaximumSize(new Dimension(MAXIMIZED_HORIZ, dialog.getHeight()));
        dialog.setLocationRelativeTo(null);
    }

    private static void runSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.setVisible(true);
    }

    /**
     * Creates a copy of jar file in other location and runs it.
     */
    private static void runUpdater(File JAR_FILE, File JAR_FILE_DEFAULT_LOCATION) {
        if (JAR_FILE.getAbsolutePath().replace("%20", " ").equals(
                JAR_FILE_DEFAULT_LOCATION.getAbsolutePath().replace("%20", " "))) {
            try {
                log.info(
                        "Creating itself at: " + new File(PathConstants.UPDATE_PATH_FILE + "Updater_.jar").getAbsolutePath());
                FileUtils.copyFile(new File(JAR_FILE.getAbsolutePath().replace("%20", " ")),
                        new File(PathConstants.UPDATE_PATH_FILE + "Updater_.jar"));
                Runtime.getRuntime().exec("java -jar " + PathConstants.UPDATE_PATH_FILE + "Updater_.jar");
                System.exit(0);
            } catch (IOException e) {
                log.warn(e);
            }
        } else {
            createUpdateDialog();
        }
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
                    str = CoreUtils.getApplicationVersionString();
                }
                if (str == null) {
                    str = CoreUtils.getApplicationVersionString();
                } else if (str.isEmpty()) {
                    str = CoreUtils.getApplicationVersionString();
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

    public enum UPDATE_MODE {NORMAL, SILENT, AFTER_UPDATE, ERROR}
}

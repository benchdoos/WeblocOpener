/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
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

import com.github.benchdoos.jcolorful.core.JColorful;
import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.gui.*;
import com.github.benchdoos.weblocopener.gui.unix.CreateNewFileDialog;
import com.github.benchdoos.weblocopener.gui.unix.ModeSelectorDialog;
import com.github.benchdoos.weblocopener.nongui.NonGuiUpdater;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.service.Analyzer;
import com.github.benchdoos.weblocopener.service.UrlsProceed;
import com.github.benchdoos.weblocopener.service.clipboard.ClipboardManager;
import com.github.benchdoos.weblocopener.service.links.WeblocLink;
import com.github.benchdoos.weblocopener.utils.CleanManager;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.Internal;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.browser.BrowserManager;
import com.github.benchdoos.weblocopener.utils.notification.NotificationManager;
import com.github.benchdoos.weblocopener.utils.system.OperatingSystem;
import com.github.benchdoos.weblocopener.utils.system.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static com.github.benchdoos.weblocopener.core.constants.ArgumentConstants.*;
import static java.awt.Frame.MAXIMIZED_HORIZ;

public class Application {
    private static final String CORRECT_CREATION_SYNTAX = "-create <file path> <url>";
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static UPDATE_MODE updateMode = UPDATE_MODE.NORMAL;


    Application(String[] args) {
        log.info("{} starts in mode: {}", ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME, Main.getCurrentMode());
        log.info("{} starts with arguments: {}", ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME, Arrays.toString(args));

        BrowserManager.loadBrowserList();

        CoreUtils.enableLookAndFeel();

        if (args.length > 1) {
            manageArguments(args);
        } else if (args.length == 1) {
            manageSoloArgument(args);
        } else {
            checkIfUpdatesAvailable();
            runSettingsDialog();
        }

    }

    private static String helpText() {
        return OPENER_CREATE_ARGUMENT + "\t[filepath] [link] \tCreates a new .webloc file on [filepath]. " +
                "[filepath] should end with [\\filename.webloc]\n" +
                OPENER_EDIT_ARGUMENT + "\t[filepath] \t\t\tCalls Edit window to edit given .webloc file.\n" +
                OPENER_SETTINGS_ARGUMENT + "\t\t\t\t\tCalls Settings window of WeblocOpener.\n" +
                OPENER_UPDATE_ARGUMENT + "\t\t\t\t\t\tCalls update-tool for WeblocOpener.";
    }

    /**
     * Manages incoming arguments
     *
     * @param args App start arguments
     */
    public static void manageArguments(String[] args) {
        log.debug("Managing arguments: " + Arrays.toString(args));
        if (args.length > 0) {
            log.info("Got args: " + Arrays.toString(args));
            if (!args[0].isEmpty()) {
                switch (args[0]) {
                    case OPENER_OPEN_ARGUMENT:
                        checkIfUpdatesAvailable();
                        runAnalyzer(args[1]);
                        break;
                    case OPENER_EDIT_ARGUMENT:
                        checkIfUpdatesAvailable();
                        manageEditArgument(args);
                        break;
                    case OPENER_SETTINGS_ARGUMENT:
                        checkIfUpdatesAvailable();
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
                        runUpdateDialog();
                        break;
                    case OPENER_HELP_ARGUMENT_HYPHEN: {
                        System.out.println(helpText());
                        break;
                    }
                    case OPENER_QR_ARGUMENT:
                        checkIfUpdatesAvailable();
                        if (args.length > 1) {
                            runQrDialog(args[1]);
                        }
                        break;
                    case OPENER_COPY_LINK_ARGUMENT:
                        checkIfUpdatesAvailable();
                        if (args.length > 1) {
                            runCopyLink(args[1]);
                        }
                        break;

                    case OPENER_COPY_QR_ARGUMENT:
                        checkIfUpdatesAvailable();
                        runCopyQrCode(args);
                        break;

                    case UPDATE_SILENT_ARGUMENT:
                        runUpdateSilent();
                        break;
                    default:
                        checkIfUpdatesAvailable();
                        runAnalyzer(args[0]);
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

    private static void runAnalyzer(String arg) {
        try {
            String url = new Analyzer(arg).getUrl();
            if (!url.isEmpty()) {
                UrlsProceed.openUrl(url);
            } else {
                runEditDialog(arg);
            }
        } catch (Exception e) {
            log.warn("Could not open file: {}", arg, e);
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
            new WeblocLink().createLink(new File(filePath), new URL(url));
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
            final String path = args[1];
            final File file;
            try {
                file = new Analyzer(path).getFile();
                if (file != null) {
                    runEditDialog(file.getAbsolutePath());
                }
            } catch (Exception e) {
                log.warn("Could not edit file: {}", path, e);
            }
        } else {
            showIncorrectArgumentMessage(OPENER_EDIT_ARGUMENT);
        }
    }

    private static void showIncorrectArgumentMessage(String argument) {
        Translation translation = new Translation("CommonsBundle");
        final String message = translation.getTranslatedString("incorrectArgument").replace("{}", argument);
        NotificationManager.getForcedNotification(null).showErrorNotification(
                translation.getTranslatedString("errorTitle"),
                message);
    }

    private static void runCopyLink(String path) {
        String url;
        try {
            url = new Analyzer(path).getUrl();
            ClipboardManager.getClipboardForSystem().copy(url);
            log.info("Successfully copied url to clipboard from: " + path);

            try {
                NotificationManager.getNotificationForCurrentOS().showInfoNotification(
                        ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
                        Translation.getTranslatedString("CommonsBundle", "linkCopied"));
            } catch (Exception e) {
                log.warn("Could not show message for user", e);
            }
        } catch (Exception e) {
            log.warn("Could not copy url from file: {}", path, e);
        }
    }

    private static void runCopyQrCode(String[] args) {
        try {
            if (args.length > 1) {
                final String path = args[1];
                String url;
                url = new Analyzer(path).getUrl();
                final BufferedImage image = UrlsProceed.generateQrCode(url);

                ClipboardManager.getClipboardForSystem().copy(image);

                NotificationManager.getNotificationForCurrentOS().showInfoNotification(ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
                        Translation.getTranslatedString("ShowQrDialogBundle", "successCopyImage"));
            }
        } catch (Exception e) {
            log.warn("Could not copy qr code for {}", args[1], e);
            NotificationManager.getNotificationForCurrentOS().showErrorNotification(ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
                    Translation.getTranslatedString("ShowQrDialogBundle", "errorCopyImage"));
        }
    }

    /**
     * Runs EditDialog
     *
     * @param filepath file path
     */
    private static void runEditDialog(String filepath) {
        EditDialog dialog;
        if (PreferencesManager.isDarkModeEnabledNow()) {
            JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
            colorful.colorizeGlobal();
            dialog = new EditDialog(filepath);
            colorful.colorize(dialog);
            dialog.updateTextFont();
        } else {
            dialog = new EditDialog(filepath);
        }

        dialog.setVisible(true);
        dialog.setMaximumSize(new Dimension(MAXIMIZED_HORIZ, dialog.getHeight()));
        dialog.setLocationRelativeTo(null);
    }

    private static void runQrDialog(String arg) {
        try {
            final File file = new Analyzer(arg).getFile();
            ShowQrDialog qrDialog;
            if (PreferencesManager.isDarkModeEnabledNow()) {
                JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
                colorful.colorizeGlobal();
                qrDialog = new ShowQrDialog(file);
                colorful.colorize(qrDialog);
            } else {
                qrDialog = new ShowQrDialog(file);
            }
            qrDialog.setVisible(true);
        } catch (Exception e) {
            log.warn("Can not create a qr-code from url: [" + arg + "]", e);
        }
    }

    public static void runSettingsDialog() {
        CleanManager.clean();

        SettingsDialog settingsDialog;
        if (PreferencesManager.isDarkModeEnabledNow()) {
            JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
            colorful.colorizeGlobal();

            settingsDialog = new SettingsDialog();

            colorful.colorize(settingsDialog);
        } else {
            settingsDialog = new SettingsDialog();
        }
        settingsDialog.setVisible(true);
    }

    public static void runUpdateDialog() {
        final UpdateDialog updateDialog;

        if (PreferencesManager.isDarkModeEnabledNow()) {
            JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
            colorful.colorizeGlobal();
            updateDialog = UpdateDialog.getInstance();
            colorful.colorize(updateDialog);
        } else {
            updateDialog = UpdateDialog.getInstance();
        }

        updateDialog.setVisible(true);
        new Thread(updateDialog::checkForUpdates).start();
    }

    private static void runUpdateSilent() {
        updateMode = UPDATE_MODE.SILENT;
        boolean isAutoUpdate = PreferencesManager.isAutoUpdateActive();

        log.debug(PreferencesManager.KEY_AUTO_UPDATE + " : " + isAutoUpdate);
        if (isAutoUpdate) {
            new NonGuiUpdater();
        }
    }

    private static void checkIfUpdatesAvailable() {
        log.debug("Checking if updates available");
        if (Internal.isCurrentTimeOlder(PreferencesManager.getLatestUpdateCheck(), 24)) {
            log.info("Checking if updates are available now, last check was at: {}", PreferencesManager.getLatestUpdateCheck());
            Thread checker = new Thread(Application::runUpdateSilent);
            checker.start();
        } else {
            log.info("Updates were checked less then 24h ago");
        }
    }

    private void manageSoloArgument(String[] args) {
        if (OperatingSystem.isWindows()) {
            manageArguments(args);
        } else if (OperatingSystem.isUnix()) {
            final String arg = args[0];
            switch (arg) {
                case OPENER_CREATE_NEW_ARGUMENT:
                    runCreateNewFileWindow();
                    break;
                case OPENER_SETTINGS_ARGUMENT:
                    checkIfUpdatesAvailable();
                    runSettingsDialog();
                    break;
                case OPENER_ABOUT_ARGUMENT:
                    new AboutApplicationDialog().setVisible(true);
                    break;
                case OPENER_UPDATE_ARGUMENT:
                    runUpdateDialog();
                    break;
                case UPDATE_SILENT_ARGUMENT:
                    runUpdateSilent();
                    break;
                case OPENER_HELP_ARGUMENT_HYPHEN: {
                    System.out.println(helpText());
                    break;
                }
                case OPENER_EDIT_ARGUMENT:
                    manageEditArgument(args);
                    break;
                case OPENER_OPEN_ARGUMENT:
                    showIncorrectArgumentMessage(OPENER_OPEN_ARGUMENT);
                    break;
                default:
                    manageArgumentsOnUnix(args);
                    break;
            }
        } else {
            log.warn("System is not supported yet: {}", SystemUtils.getCurrentOS());
        }
    }

    private void runCreateNewFileWindow() {
        CreateNewFileDialog createNewFileDialog;

        if (PreferencesManager.isDarkModeEnabledNow()) {
            JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
            colorful.colorizeGlobal();

            createNewFileDialog = new CreateNewFileDialog();

            colorful.colorize(createNewFileDialog);
        } else {
            createNewFileDialog = new CreateNewFileDialog();
        }

        createNewFileDialog.setModal(false);
        createNewFileDialog.setVisible(true);
    }

    private void manageArgumentsOnUnix(String[] args) {
        final String unixOpeningMode = PreferencesManager.getUnixOpeningMode();
        log.info("Unix: opening mode is: {}", unixOpeningMode);
        if (unixOpeningMode.equalsIgnoreCase(SettingsConstants.OPENER_UNIX_DEFAULT_SELECTOR_MODE)) {
            checkIfUpdatesAvailable();
            runModeSelectorWindow(args);
        } else {
            String[] unixArgs = new String[]{unixOpeningMode, args[0]};
            manageArguments(unixArgs);
        }
    }

    private void runModeSelectorWindow(String[] args) {
        String filePath = args[0];
        ModeSelectorDialog modeSelectorDialog = new ModeSelectorDialog(new File(filePath));
        modeSelectorDialog.setVisible(true);
    }

    public enum UPDATE_MODE {NORMAL, SILENT}
}

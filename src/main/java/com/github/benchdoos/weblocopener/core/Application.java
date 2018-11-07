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
import com.github.benchdoos.weblocopener.gui.*;
import com.github.benchdoos.weblocopener.nongui.NonGuiUpdater;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.service.Analyzer;
import com.github.benchdoos.weblocopener.service.UrlsProceed;
import com.github.benchdoos.weblocopener.utils.CleanManager;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.UserUtils;
import com.github.benchdoos.weblocopener.utils.browser.BrowserManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static com.github.benchdoos.weblocopener.core.constants.ArgumentConstants.*;
import static java.awt.Frame.MAXIMIZED_HORIZ;

public class Application {
    private static final String CORRECT_CREATION_SYNTAX = "-create <file path> <url>";
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static UPDATE_MODE updateMode = UPDATE_MODE.NORMAL;


    Application(String[] args) {
        log.info("{} starts in updateMode: {}", ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME, Main.getCurrentMode());
        log.info("{} starts with arguments: {}", ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME, Arrays.toString(args));

        BrowserManager.loadBrowserList();

        CoreUtils.enableLookAndFeel();

        manageArguments(args);

    }

    private static void createUpdateDialog() {
        final UpdateDialog updateDialog = UpdateDialog.getInstance();
        updateDialog.setVisible(true);
        updateDialog.checkForUpdates();
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
    public static void initUpdate() {
        try {
            final File currentFile = CoreUtils.getCurrentFile();
            final String command;
            if (currentFile.isFile()) {
                command = currentFile.getAbsolutePath() + " " + ArgumentConstants.OPENER_UPDATE_ARGUMENT;
            } else {
                command = "cmd /c start weblocopener " + ArgumentConstants.OPENER_UPDATE_ARGUMENT;
            }
            log.info("Starting update: {}", command);
            Runtime.getRuntime().exec(command);
        } catch (IOException | URISyntaxException e) {
            log.warn("Could not run update", e);
        }
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
                        CleanManager.clean();
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
                        createUpdateDialog();
                        break;
                    case OPENER_HELP_ARGUMENT_HYPHEN: {
                        System.out.println(helpText());
                        break;
                    }
                    case OPENER_QR_ARGUMENT:
                        if (args.length > 1) {
                            String url = runAnalyzer(args[1]);

                            try {
                                ShowQrDialog qrDialog = new ShowQrDialog(url, UrlsProceed.generateQrCode(url));
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
                        boolean isAutoUpdate = PreferencesManager.isAutoUpdateActive();

                        log.debug(PreferencesManager.KEY_AUTO_UPDATE + " : " + isAutoUpdate);
                        if (isAutoUpdate) {
                            new NonGuiUpdater();
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

    public enum UPDATE_MODE {NORMAL, SILENT}
}

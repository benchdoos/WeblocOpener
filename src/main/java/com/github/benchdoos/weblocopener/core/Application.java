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

import com.github.benchdoos.beans.DefaultThemes;
import com.github.benchdoos.core.JColorful;
import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.core.constants.ArgumentConstants;
import com.github.benchdoos.weblocopener.gui.*;
import com.github.benchdoos.weblocopener.nongui.NonGuiUpdater;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.service.Analyzer;
import com.github.benchdoos.weblocopener.service.UrlsProceed;
import com.github.benchdoos.weblocopener.service.WeblocLink;
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

        manageArguments(args);

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
                        runUpdateDialog();
                        break;
                    case OPENER_HELP_ARGUMENT_HYPHEN: {
                        System.out.println(helpText());
                        break;
                    }
                    case OPENER_QR_ARGUMENT:
                        if (args.length > 1) {
                            try {
                                final String filePath = args[1];
                                ShowQrDialog qrDialog = new ShowQrDialog(new Analyzer(filePath).getFile());
                                qrDialog.setVisible(true);
                            } catch (Exception e) {
                                log.warn("Can not create a qr-code from url: [" + args[1] + "]", e);
                            }
                        }
                        break;
                    case OPENER_COPY_LINK_ARGUMENT:
                        if (args.length > 1) {
                            final String path = args[1];
                            String url;
                            try {
                                url = new Analyzer(path).getUrl();
                                StringSelection stringSelection = new StringSelection(url);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);
                                log.info("Successfully copied url to clipboard from: " + path);

                                try {
                                    UserUtils.showTrayMessage(ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
                                            Translation.getTranslatedString("CommonsBundle", "linkCopied"),
                                            TrayIcon.MessageType.INFO);
                                } catch (Exception e) {
                                    log.warn("Could not show message for user", e);
                                }
                            } catch (Exception e) {
                                log.warn("Could not copy url from file: {}", args[1], e);
                            }

                        }
                        break;

                    case OPENER_COPY_QR_ARGUMENT:
                        try {
                            if (args.length > 1) {
                                final String path = args[1];
                                String url;
                                url = new Analyzer(path).getUrl();
                                final BufferedImage image = UrlsProceed.generateQrCode(url);
                                CoreUtils.copyImageToClipBoard(image);

                                UserUtils.showTrayMessage(ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
                                        Translation.getTranslatedString("ShowQrDialogBundle", "successCopyImage"),
                                        TrayIcon.MessageType.INFO);
                            }
                        } catch (Exception e) {
                            log.warn("Could not copy qr code for {}", args[1], e);
                            UserUtils.showTrayMessage(ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
                                    Translation.getTranslatedString("ShowQrDialogBundle", "errorCopyImage")
                                    , TrayIcon.MessageType.ERROR);
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
                        final String filePath = args[0];
                        try {
                            String url = new Analyzer(filePath).getUrl();
                            if (!url.isEmpty()) {
                                UrlsProceed.openUrl(url);
                            } else {
                                runEditDialog(filePath);
                            }
                        } catch (Exception e) {
                            log.warn("Could not open file: {}", filePath, e);
                        }
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

            /*if (files != null) {
                if (files.size() == 1) {
                    runEditDialog(path);
                } else {
                    FileChooser fileChooser = new FileChooser(files);
                    fileChooser.setVisible(true);
                    final File chosenFile = fileChooser.getChosenFile();
                    if (chosenFile != null) {
                        runEditDialog(chosenFile.getAbsolutePath());
                    }
                }
            }*/
        } else {
            UserUtils.showErrorMessageToUser(null, "Error",
                    "Argument '-edit' should have location path parameter.");
        }
    }

    /**
     * Runs EditDialog
     *
     * @param filepath file path
     */
    private static void runEditDialog(String filepath) {
        EditDialog dialog = new EditDialog(filepath);
        dialog.setVisible(true);
        dialog.setMaximumSize(new Dimension(MAXIMIZED_HORIZ, dialog.getHeight()));
        dialog.setLocationRelativeTo(null);
    }

    public static void runSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog();

        if (PreferencesManager.isDarkModeEnabledNow()) {
            JColorful colorful = new JColorful(DefaultThemes.EXTREMELY_BLACK);
            colorful.colorize(settingsDialog);
        }

        settingsDialog.setVisible(true);
    }

    public static void runUpdateDialog() {
        final UpdateDialog updateDialog = UpdateDialog.getInstance();
        updateDialog.setVisible(true);
        new Thread(updateDialog::checkForUpdates).start();
    }

    public enum UPDATE_MODE {NORMAL, SILENT}
}

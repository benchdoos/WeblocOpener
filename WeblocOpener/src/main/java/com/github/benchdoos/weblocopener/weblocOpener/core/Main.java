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

package com.github.benchdoos.weblocopener.weblocOpener.core;

import com.github.benchdoos.weblocopener.commons.utils.Logging;
import com.github.benchdoos.weblocopener.commons.utils.UserUtils;
import com.github.benchdoos.weblocopener.commons.utils.browser.BrowserManager;
import com.github.benchdoos.weblocopener.commons.utils.system.SystemUtils;
import com.github.benchdoos.weblocopener.commons.utils.system.UnsupportedOsSystemException;
import com.github.benchdoos.weblocopener.commons.utils.system.UnsupportedSystemVersionException;
import com.github.benchdoos.weblocopener.weblocOpener.gui.*;
import com.github.benchdoos.weblocopener.weblocOpener.service.Analyzer;
import com.github.benchdoos.weblocopener.weblocOpener.service.UrlsProceed;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;

import static com.github.benchdoos.weblocopener.commons.core.ApplicationConstants.*;
import static java.awt.Frame.MAXIMIZED_HORIZ;

public class Main {
    private static final String CORRECT_CREATION_SYNTAX = "-create <file path> <url>";
    private static Logger log;

    public static void main(String[] args) {

        try {
            initLogging();

            SystemUtils.checkIfSystemIsSupported();

            enableLookAndFeel();

            BrowserManager.loadBrowserList();

            manageArguments(args);
        } catch (UnsupportedOsSystemException | UnsupportedSystemVersionException e) {
            UserUtils.showErrorMessageToUser(null, "System is not supported", e.getMessage());
        } catch (Throwable throwable) {
            log.fatal("Got an FATAL ERROR", throwable);
        }
    }

    private static void initLogging() {
        new Logging(WEBLOC_OPENER_APPLICATION_NAME);
        log = Logger.getLogger(Logging.getCurrentClassName());
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

                    case OPENER_SUCCESS_UPDATE_ARGUMENT:
                        new SuccessUpdateInstalledDialog().setVisible(true);
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
                    case OPENER_HELP_ARGUMENT_HYPHEN:
                    case OPENER_HELP_ARGUMENT_SLASH: {
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

    private static String helpText() {
        return OPENER_CREATE_ARGUMENT + "\t[filepath] [link] \tCreates a new .webloc file on [filepath]. " +
                "[filepath] should end with [\\filename.webloc]\n" +
                OPENER_EDIT_ARGUMENT + "\t[filepath] \t\t\tCalls Edit window to edit given .webloc file.\n" +
                OPENER_SETTINGS_ARGUMENT + "\t\t\t\t\tCalls Settings window of WeblocOpener.\n" +
                OPENER_UPDATE_ARGUMENT + "\t\t\t\t\t\tCalls update-tool for WeblocOpener.";
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

    private static void runSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.setVisible(true);
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
     * Manages edit argument, runs edit-mode to file in second argument
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

    /**
     * Enables LookAndFeel for current OS.
     *
     * @see javax.swing.UIManager.LookAndFeelInfo
     */
    private static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

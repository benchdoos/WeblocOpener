package com.doos.webloc_opener.core;

import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.core.SettingsManager;
import com.doos.commons.registry.RegistryException;
import com.doos.commons.registry.RegistryManager;
import com.doos.commons.registry.fixer.RegistryFixer;
import com.doos.commons.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerInstallPathKeyFailException;
import com.doos.commons.utils.Logging;
import com.doos.commons.utils.UserUtils;
import com.doos.commons.utils.system.SystemUtils;
import com.doos.commons.utils.system.UnsupportedOsSystemException;
import com.doos.commons.utils.system.UnsupportedSystemVersionException;
import com.doos.webloc_opener.gui.AboutApplicationDialog;
import com.doos.webloc_opener.gui.EditDialog;
import com.doos.webloc_opener.gui.SettingsDialog;
import com.doos.webloc_opener.service.Analyzer;
import com.doos.webloc_opener.service.UrlsProceed;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

import static com.doos.commons.core.ApplicationConstants.*;
import static com.doos.commons.utils.UserUtils.showErrorMessageToUser;
import static java.awt.Frame.MAXIMIZED_HORIZ;

public class Main {

    public static void main(String[] args) {

        try {
            new Logging(ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME);

            SystemUtils.checkIfSystemIsSupported();

            enableLookAndFeel();

            manageArguments(args);
        } catch (UnsupportedOsSystemException | UnsupportedSystemVersionException e) {
            UserUtils.showErrorMessageToUser(null, "Error", e.getMessage());
        }
    }


    /**
     * Loads registry info, trys to fix if can no load.
     * If registryFixer crashes - uses  default properties.
     * If registryFixer can not fix install location - calls System.exit(-1);
     */
    private static void tryLoadProperties() {
        try {
            SettingsManager.loadInfo();
        } catch (RegistryException e) {
            e.printStackTrace();
            try {
                RegistryFixer.fixRegistry();
            } catch (RegistryFixerAutoUpdateKeyFailException | RegistryFixerAppVersionKeyFailException e1) {
                RegistryManager.setDefaultSettings();
            } catch (RegistryFixerInstallPathKeyFailException | FileNotFoundException e1) {
                showErrorMessageToUser(null, "Can not fix registry",
                                       "Registry application data is corrupt. " +
                                               "Please re-install the " + "application.");
                System.exit(-1);
            }

        }
    }

    /**
     * Manages incoming arguments
     *
     * @param args App start arguments
     */
    private static void manageArguments(String[] args) {
        if (args.length > 0) {
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
                    default:
                        runAnalizer(args[0]);
                        break;
                }
            }
        } else {
            runSettingsDialog();
        }
    }

    private static void runSettingsDialog() {
        tryLoadProperties();
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.setVisible(true);
    }

    /**
     * Manages default incorrect argument (or url)
     *
     * @param arg main args
     */
    private static void runAnalizer(String arg) {
        String url = new Analyzer(arg).getUrl();
        UrlsProceed.openUrl(url);
        UrlsProceed.shutdownLogout();
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
            JOptionPane.showMessageDialog(new Frame(), "Argument '-edit' should have " +
                                                  "location path parameter.", "Error",
                                          JOptionPane.ERROR_MESSAGE);
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

package com.doos;

import com.doos.gui.EditDialog;
import com.doos.gui.SettingsDialog;
import com.doos.service.Analyzer;
import com.doos.service.Logging;
import com.doos.service.UrlsProceed;
import com.doos.utils.SettingsManager;
import com.doos.utils.registry.RegistryException;
import com.doos.utils.registry.RegistryManager;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

import static java.awt.Frame.MAXIMIZED_HORIZ;

public class Main {
    public static String[] args;
    public static Properties properties = new Properties();

    public static void main(String[] args) {
        Main.args = args;
        new Logging();
        try {
            loadProperties();
            if (!properties.getProperty(RegistryManager.KEY_CURRENT_VERSION).equals(ApplicationConstants.APP_VERSION)) {
                properties.setProperty(RegistryManager.KEY_CURRENT_VERSION, ApplicationConstants.APP_VERSION);
                saveProperties();
            }
        } catch (RegistryException e) {
            e.printStackTrace();
        }

        enableLookAndFeel();

        manageArguments(args);
    }

    /**
     * Manages incoming arguments
     *
     * @param args
     */
    private static void manageArguments(String[] args) {
        if (args.length > 0) {
            if (!args[0].isEmpty()) {
                switch (args[0]) {
                    case "-edit":
                        if (args.length > 1) {
                            EditDialog d = new EditDialog(args[1]);
                            d.setVisible(true);
                            d.setMaximumSize(new Dimension(MAXIMIZED_HORIZ, d.getHeight()));
                            d.setLocationRelativeTo(null);
                        } else {
                            JOptionPane.showMessageDialog(new Frame(), "Argument '-edit' should have " +
                                            "location path parameter.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(-1);
                        }
                        break;
                    default:
                        String url = new Analyzer(args[0]).getUrl();
                        UrlsProceed.openUrl(url);
                        UrlsProceed.shutdownLogout();
                        break;
                }
            }
        } else {
            SettingsDialog settingsDialog = new SettingsDialog();
            settingsDialog.setVisible(true);
        }
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

    public static void loadProperties() throws RegistryException {
        properties = SettingsManager.loadInfo();
    }

    public static void saveProperties() throws RegistryException {
        SettingsManager.updateInfo(properties);
    }


    public static void useDefaultAppProperties() {
        properties.setProperty(RegistryManager.KEY_AUTO_UPDATE, Boolean.toString(true));
        properties.setProperty(RegistryManager.KEY_CURRENT_VERSION, ApplicationConstants.APP_VERSION);
        try {
            saveProperties();
        } catch (RegistryException e) {
            e.getStackTrace();
        }
    }


}

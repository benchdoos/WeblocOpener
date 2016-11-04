package com.doos;

import com.doos.gui.EditDialog;
import com.doos.gui.SettingsDialog;
import com.doos.service.Analyzer;
import com.doos.service.Logging;
import com.doos.service.UrlsProceed;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static java.awt.Frame.MAXIMIZED_HORIZ;

public class Main {
    public static final String UPDATE_ACTIVE = "UPDATE_ACTIVE";
    public static final String LAST_UPDATED = "LAST_UPDATED";
    public static final String CURRENT_APP_VERSION = "CURRENT_APP_VERSION";
    public static String[] args;
    public static Properties properties = new Properties();

    public static void main(String[] args) {
        Main.args = args;
        new Logging();
        try {
            loadProperties();
            if (!properties.getProperty(CURRENT_APP_VERSION).equals(ApplicationConstants.APP_VERSION)) {
                properties.setProperty(CURRENT_APP_VERSION, ApplicationConstants.APP_VERSION);
                saveProperties();
            }
        } catch (IOException e) {
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

    public static void loadProperties() throws IOException {
        properties.load(new FileInputStream(ApplicationConstants.SETTINGS_FILE_PATH));
    }

    public static void saveProperties() throws IOException {
        properties.store(new FileOutputStream(ApplicationConstants.SETTINGS_FILE_PATH), "WeblocOpener Settings");

    }


    public static void createNewFileProperties() {
        properties.setProperty(UPDATE_ACTIVE, "true");
        properties.setProperty(LAST_UPDATED, "none");
        properties.setProperty(CURRENT_APP_VERSION, ApplicationConstants.APP_VERSION);
        try {
            saveProperties();
        } catch (IOException e1) {
            e1.getStackTrace();
        }
    }


}

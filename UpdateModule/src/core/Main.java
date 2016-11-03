package core;

import gui.UpdateDialog;
import utils.ApplicationConstants;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 */
public class Main {
    final public static String UPDATE_ACTIVE = "UPDATE_ACTIVE";
    final public static String LAST_UPDATED = "LAST_UPDATED";
    final public static String CURRENT_APP_VERSION = "CURRENT_APP_VERSION";
    public static Properties properties = new Properties();

    public static void main(String[] args) {

        enableLookAndFeel();
        if (args.length > 0) {

        } else {
            UpdateDialog updateDialog = new UpdateDialog();
            updateDialog.setVisible(true);
            updateDialog.checkForUpdates();
        }
    }


    public static void updateProperties() {
        try {
            properties.store(new FileOutputStream(ApplicationConstants.SETTINGS_FILE_PATH), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

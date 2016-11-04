package com.doos.core;

import com.doos.gui.UpdateDialog;
import com.doos.nongui.NonGuiUpdater;
import com.doos.utils.ApplicationConstants;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static com.doos.utils.ApplicationConstants.UPDATE_PATH_FILE;

/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 */
public class Main {
    final public static String UPDATE_ACTIVE = "UPDATE_ACTIVE";
    final public static String LAST_UPDATED = "LAST_UPDATED";
    final public static String CURRENT_APP_VERSION = "CURRENT_APP_VERSION";
    public static Properties properties = new Properties();

    public static void main(String[] args) {
        loadProperties();
        enableLookAndFeel();
        System.out.println("Updater args: " + Arrays.toString(args));
        if (args.length > 0) {
            if (args[0].equals("-s")) {
                System.out.println("Prop:" + properties.getProperty(UPDATE_ACTIVE));
                if (properties.getProperty(UPDATE_ACTIVE).equals("true")) {
                    new NonGuiUpdater();
                }
            } else {
                System.out.println("No such argument: " + args[0]);
            }
        } else {
            final File JAR_FILE = new File(UpdateDialog.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath());
            if (JAR_FILE.getName().equals("Updater.jar")) {
                System.out.println("Creating itself");
                try {
                    FileUtils.copyFile(new File(JAR_FILE.getAbsolutePath().replace("%20", " ")),
                            new File(UPDATE_PATH_FILE + "Updater_.jar"));
                    Runtime.getRuntime().exec("java -jar " + UPDATE_PATH_FILE + "Updater_.jar");
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                UpdateDialog updateDialog = new UpdateDialog();

                updateDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        System.out.println("deleteOnExit:" + JAR_FILE.getAbsolutePath());
                        JAR_FILE.deleteOnExit();
                        System.exit(0);
                    }
                });
                updateDialog.setVisible(true);
                updateDialog.checkForUpdates();
                System.out.println("current jar: " + JAR_FILE.getAbsolutePath());
            }
        }
    }


    public static void updateProperties() {
        try {
            properties.store(new FileOutputStream(ApplicationConstants.SETTINGS_FILE_PATH), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadProperties() {
        try {
            properties.load(new FileInputStream(ApplicationConstants.SETTINGS_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.doos.core;

import com.doos.gui.UpdateDialog;
import com.doos.nongui.NonGuiUpdater;
import com.doos.utils.SettingsManager;
import com.doos.utils.registry.RegistryException;
import com.doos.utils.registry.RegistryManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static com.doos.utils.ApplicationConstants.UPDATE_PATH_FILE;

/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 */
public class Main {
    public static Properties properties = new Properties();
    public static boolean isAppAfterUpdate = false;
    public static Mode mode = Mode.NORMAL;

    public static void main(String[] args) {
        try {
            loadProperties();
        } catch (RegistryException e) {
            String message = "Can not read data from registry.";
            System.out.println(message);
            JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
        }
        enableLookAndFeel();
        System.out.println("Updater args: " + Arrays.toString(args));
        if (args.length > 0) {

            switch (args[0]) {
                case "-s":
                    mode = Mode.SILENT;
                    System.out.println("Prop:" + properties.getProperty(RegistryManager.KEY_AUTO_UPDATE));
                    if (Boolean.parseBoolean(properties.getProperty(RegistryManager.KEY_AUTO_UPDATE))) {
                        new NonGuiUpdater();
                    }
                    break;
                case "-afterUpdate":
                    mode = Mode.AFTER_UPDATE;
                    isAppAfterUpdate = true;
                    new File(UPDATE_PATH_FILE + "Updater_.jar").delete();
                    createUpdateDialog();
                    break;
                default:
                    mode = Mode.ERROR;
                    System.out.println("No such argument: " + args[0]);
                    break;
            }
        } else {
            mode = Mode.NORMAL;
            /*-----*/
            System.out.println("Installation folder: " + RegistryManager.getInstallLocationValue());
            /*-----*/


            final File JAR_FILE = new File(UpdateDialog.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath());
            final File JAR_FILE_DEFAULT_LOCATION = new File(properties.getProperty(RegistryManager.KEY_INSTALL_LOCATION)
                    + File.separator + "Updater.jar");

            System.out.println("Jar: " + JAR_FILE.getAbsolutePath() + " def: " + JAR_FILE_DEFAULT_LOCATION.getAbsolutePath());

            if (JAR_FILE.getAbsolutePath().replace("%20", " ").equals(
                    JAR_FILE_DEFAULT_LOCATION.getAbsolutePath().replace("%20", " "))) {
                try {
                    System.out.println("Creating itself at: " + new File(UPDATE_PATH_FILE + "Updater_.jar").getAbsolutePath());
                    FileUtils.copyFile(new File(JAR_FILE.getAbsolutePath().replace("%20", " ")),
                            new File(UPDATE_PATH_FILE + "Updater_.jar"));
                    Runtime.getRuntime().exec("java -jar " + UPDATE_PATH_FILE + "Updater_.jar");
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                createUpdateDialog();
            }
        }
    }

    private static void createUpdateDialog() {
        UpdateDialog updateDialog = new UpdateDialog();
        updateDialog.setVisible(true);
        updateDialog.checkForUpdates();
    }

    public static void updateProperties() {
        SettingsManager.updateInfo(properties);
    }

    public static void loadProperties() throws RegistryException {
        properties = SettingsManager.loadInfo();
    }

    private static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Mode {NORMAL, SILENT, AFTER_UPDATE, ERROR}
}

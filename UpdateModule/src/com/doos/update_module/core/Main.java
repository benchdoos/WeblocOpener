package com.doos.update_module.core;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.core.SettingsManager;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryCanNotWriteInfoException;
import com.doos.settings_manager.registry.RegistryException;
import com.doos.settings_manager.registry.RegistryManager;
import com.doos.settings_manager.registry.fixer.RegistryFixer;
import com.doos.settings_manager.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.settings_manager.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.doos.settings_manager.registry.fixer.RegistryFixerInstallPathKeyFailException;
import com.doos.update_module.gui.UpdateDialog;
import com.doos.update_module.nongui.NonGuiUpdater;
import com.doos.update_module.utils.Internal;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static com.doos.settings_manager.ApplicationConstants.UPDATE_PATH_FILE;
import static com.doos.settings_manager.core.SettingsManager.showErrorMessageToUser;
import static com.doos.update_module.nongui.NonGuiUpdater.tray;
import static com.doos.update_module.nongui.NonGuiUpdater.trayIcon;

/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 */
public class Main {
    public static Mode mode = Mode.NORMAL;

    public static void main(String[] args) {
        enableLookAndFeel();

        tryLoadProperties();

        System.out.println("Updater args: " + Arrays.toString(args));

        manageArguments(args);
    }

    private static void manageArguments(String[] args) {
        if (args.length > 0) {

            switch (args[0]) {
                case ApplicationConstants.UPDATE_SILENT_ARGUMENT:
                    mode = Mode.SILENT;
                    boolean isAutoUpdate = ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE;

                    try {
                        isAutoUpdate = RegistryManager.isAutoUpdateActive();
                    } catch (RegistryCanNotReadInfoException e) {
                        try {
                            RegistryManager
                                    .setAutoUpdateActive(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
                        } catch (RegistryCanNotWriteInfoException ignore) {/*NOP*/}
                    }

                    System.out.println(RegistryManager.KEY_AUTO_UPDATE + " : " + isAutoUpdate);
                    if (isAutoUpdate) {
                        new NonGuiUpdater();
                    }
                    break;
                case ApplicationConstants.UPDATE_DELETE_TEMP_FILE_ARGUMENT:
                    mode = Mode.AFTER_UPDATE;
                    new File(UPDATE_PATH_FILE + "Updater_.jar").delete();
                    //createUpdateDialog();
                    break;
                default:
                    mode = Mode.ERROR;
                    System.out.println("No such argument: [" + args[0] + "]");
                    break;
            }
        } else {
            mode = Mode.NORMAL;
            /*-----*/
            try {
                System.out.println("Installation folder: " + RegistryManager.getInstallLocationValue());
            } catch (RegistryCanNotReadInfoException ignore) {/*NOP*/}
            /*-----*/
            initUpdateJar();
        }
    }

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
                showErrorMessageToUser("Can not fix registry",
                                       "Registry application data is corrupt. " +
                                               "Please re-install the " + "application.");
                System.exit(-1);
            }

        }
    }

    /**
     * Finds out, where app is located.
     */
    public static void initUpdateJar() {
        final File JAR_FILE = new File(Main.class.getProtectionDomain()
                                               .getCodeSource().getLocation().getPath());
        String property = null;
        try {
            property = RegistryManager.getInstallLocationValue();
        } catch (RegistryCanNotReadInfoException e) {
            e.printStackTrace();
        }
        File JAR_FILE_DEFAULT_LOCATION = getFileLocation(JAR_FILE, property);

        System.out
                .println("Jar: " + JAR_FILE.getAbsolutePath() + " def: " + JAR_FILE_DEFAULT_LOCATION.getAbsolutePath());

        runUpdater(JAR_FILE, JAR_FILE_DEFAULT_LOCATION);
    }

    /**
     * Creates a copy of jar file in other location and runs it.
     */
    private static void runUpdater(File JAR_FILE, File JAR_FILE_DEFAULT_LOCATION) {
        if (JAR_FILE.getAbsolutePath().replace("%20", " ").equals(
                JAR_FILE_DEFAULT_LOCATION.getAbsolutePath().replace("%20", " "))) {
            try {
                System.out.println(
                        "Creating itself at: " + new File(UPDATE_PATH_FILE + "Updater_.jar").getAbsolutePath());
                FileUtils.copyFile(new File(JAR_FILE.getAbsolutePath().replace("%20", " ")),
                                   new File(UPDATE_PATH_FILE + "Updater_.jar"));
                Runtime.getRuntime().exec("java -jar " + UPDATE_PATH_FILE + "Updater_.jar");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                SettingsManager.loadInfo();
            } catch (RegistryException e) {
                String message
                        = "Can not read data from registry.";
                System.out.println(message);
                JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);

            }
            Main.createUpdateDialog();
        }
    }

    private static File getFileLocation(File JAR_FILE, String property) {
        File JAR_FILE_DEFAULT_LOCATION;
        if (property != null) {
            JAR_FILE_DEFAULT_LOCATION = new File(property
                                                         + File.separator + "Updater.jar");
        } else {
            File file = JAR_FILE.getParentFile();
            System.out.println("Parent file is: " + file.getAbsolutePath());
            if (file.getName().equals(ApplicationConstants.APP_NAME)) {
                JAR_FILE_DEFAULT_LOCATION = new File(file.getAbsolutePath() + File.separator + "Updater.jar");
            } else {
                String programFilesPath = System.getenv("ProgramFiles(X86)");
                JAR_FILE_DEFAULT_LOCATION = new File(
                        programFilesPath + "WeblocOpener" + File.separator + "Updater.jar"); //TODO find better solution
            }
            ;
        }
        return JAR_FILE_DEFAULT_LOCATION;
    }

    public static void createUpdateDialog() {
        final UpdateDialog updateDialog = new UpdateDialog();

        updateDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    SettingsManager.loadInfo();
                } catch (RegistryException e1) {
                    e1.printStackTrace();
                }
                String str;
                try {
                    str = RegistryManager.getAppVersionValue();
                } catch (RegistryCanNotReadInfoException e1) {
                    str = ApplicationConstants.APP_VERSION;
                }
                if (str == null) {
                    //str = serverAppVersion.getVersion(); //why????
                    str = ApplicationConstants.APP_VERSION;
                } else if (str.isEmpty()) {
                    str = ApplicationConstants.APP_VERSION;
                }
                if (Internal.versionCompare(str, updateDialog.getAppVersion().getVersion()) == 0) {
                    tray.remove(trayIcon);
                    System.exit(0);
                }
                super.windowClosed(e);

            }

        });
        updateDialog.setVisible(true);
        updateDialog.checkForUpdates();
    }


    private static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {/*NOP*/}
    }

    public enum Mode {NORMAL, SILENT, AFTER_UPDATE, ERROR}
}

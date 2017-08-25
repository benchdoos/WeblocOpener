package com.doos.update_module.core;

import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.registry.RegistryCanNotReadInfoException;
import com.doos.commons.registry.RegistryCanNotWriteInfoException;
import com.doos.commons.registry.RegistryManager;
import com.doos.commons.registry.fixer.RegistryFixer;
import com.doos.commons.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerInstallPathKeyFailException;
import com.doos.commons.utils.Internal;
import com.doos.commons.utils.Logging;
import com.doos.commons.utils.UserUtils;
import com.doos.commons.utils.system.SystemUtils;
import com.doos.commons.utils.system.UnsupportedOsSystemException;
import com.doos.commons.utils.system.UnsupportedSystemVersionException;
import com.doos.update_module.gui.UpdateDialog;
import com.doos.update_module.nongui.NonGuiUpdater;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static com.doos.commons.core.ApplicationConstants.UPDATE_PATH_FILE;
import static com.doos.commons.core.ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME;
import static com.doos.commons.utils.Logging.getCurrentClassName;
import static com.doos.commons.utils.UserUtils.showErrorMessageToUser;
import static com.doos.update_module.nongui.NonGuiUpdater.tray;
import static com.doos.update_module.nongui.NonGuiUpdater.trayIcon;

/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class Main {
    public static Mode mode = Mode.NORMAL;
    private static Logger log;

    public static void main(String[] args) {
        try {
            new Logging(ApplicationConstants.UPDATER_APPLICATION_NAME);
            log = Logger.getLogger(getCurrentClassName());

            SystemUtils.checkIfSystemIsSupported();

            enableLookAndFeel();

            tryLoadProperties();

            System.out.println("Updater args: " + Arrays.toString(args));

            manageArguments(args);
        } catch (UnsupportedOsSystemException | UnsupportedSystemVersionException e) {
            UserUtils.showErrorMessageToUser(null, "Error", e.getMessage());
        }
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

                    log.debug(RegistryManager.KEY_AUTO_UPDATE + " : " + isAutoUpdate);
                    if (isAutoUpdate) {
                        new NonGuiUpdater();
                    }
                    break;
                case ApplicationConstants.UPDATE_DELETE_TEMP_FILE_ARGUMENT:
                    mode = Mode.AFTER_UPDATE;
                    new File(UPDATE_PATH_FILE + "Updater_.jar").delete();
                    File[] files = new File(UPDATE_PATH_FILE).listFiles();
                    if (files != null) {
                        for (File current : files) {
                            if (current.isFile() && current.getName().toLowerCase().contains(
                                    WEBLOC_OPENER_APPLICATION_NAME.toLowerCase()) &&
                                    current.getName().toLowerCase().contains("exe")) {
                                current.delete();
                            }
                        }
                    }

                    new File(ApplicationConstants.DEFAULT_LIST_LOCATION).delete();
                    //createUpdateDialog();
                    break;
                default:
                    mode = Mode.ERROR;
                    String message = "No such argument: [" + args[0] + "]";
                    log.warn(message);
                    UserUtils.showWarningMessageToUser(null, null,
                            message);
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
            RegistryFixer.fixRegistry();
        } catch (RegistryFixerAutoUpdateKeyFailException | RegistryFixerAppVersionKeyFailException e1) {
            log.warn("Can not fix registry on startup", e1);
            RegistryManager.setDefaultSettings();
        } catch (RegistryFixerInstallPathKeyFailException | FileNotFoundException e1) {
            log.warn("Can not fix registry on startup", e1);
            showErrorMessageToUser(null, "Can not fix registry",
                    "Registry application data is corrupt. " +
                            "Please re-install the " + "application.");
            System.exit(-1);
        }
    }

    /**
     * Finds out, where app is located.
     */
    public static void initUpdateJar() {
        final File JAR_FILE = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String property = null;
        try {
            property = RegistryManager.getInstallLocationValue();
        } catch (RegistryCanNotReadInfoException e) {
            e.printStackTrace();
        }
        File JAR_FILE_DEFAULT_LOCATION = getFileLocation(JAR_FILE, property);

        log.info("Jar: " + JAR_FILE.getAbsolutePath() + " def: " + JAR_FILE_DEFAULT_LOCATION.getAbsolutePath());

        runUpdater(JAR_FILE, JAR_FILE_DEFAULT_LOCATION);
    }

    /**
     * Creates a copy of jar file in other location and runs it.
     */
    private static void runUpdater(File JAR_FILE, File JAR_FILE_DEFAULT_LOCATION) {
        if (JAR_FILE.getAbsolutePath().replace("%20", " ").equals(
                JAR_FILE_DEFAULT_LOCATION.getAbsolutePath().replace("%20", " "))) {
            try {
                log.info(
                        "Creating itself at: " + new File(UPDATE_PATH_FILE + "Updater_.jar").getAbsolutePath());
                FileUtils.copyFile(new File(JAR_FILE.getAbsolutePath().replace("%20", " ")),
                        new File(UPDATE_PATH_FILE + "Updater_.jar"));
                Runtime.getRuntime().exec("java -jar " + UPDATE_PATH_FILE + "Updater_.jar");
                System.exit(0);
            } catch (IOException e) {
                log.warn(e);
            }
        } else {
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
            log.info("Parent file is: " + file.getAbsolutePath());
            if (file.getName().equals(ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME)) {
                JAR_FILE_DEFAULT_LOCATION = new File(file.getAbsolutePath() + File.separator + "Updater.jar");
            } else {
                String programFilesPath = getProgramFilesPath();
                JAR_FILE_DEFAULT_LOCATION = new File(
                        programFilesPath + "WeblocOpener" + File.separator + "Updater.jar"); //TODO find better solution
            }
        }
        return JAR_FILE_DEFAULT_LOCATION;
    }

    /**
     * Finds out %ProgramFiles% path
     *
     * @return Path to %ProgramFiles% folder
     */
    private static String getProgramFilesPath() {
        String programFilesPath;
        String realArch = SystemUtils.getRealSystemArch();
        if (realArch.equals("64")) {
            programFilesPath = System.getenv("ProgramFiles(X86)");
        } else {
            programFilesPath = System.getenv("ProgramFiles");
        }
        return programFilesPath;
    }

    private static void createUpdateDialog() {
        final UpdateDialog updateDialog = new UpdateDialog();


        updateDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                String str;
                try {
                    str = RegistryManager.getAppVersionValue();
                } catch (RegistryCanNotReadInfoException e1) {
                    str = ApplicationConstants.APP_VERSION;
                }
                if (str == null) {
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
        UpdateDialog.updateDialog = updateDialog;

        updateDialog.setVisible(true);
        updateDialog.checkForUpdates();
    }


    private static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
            log.warn("Could not establish UI Look and feel.");
        }
    }

    public enum Mode {NORMAL, SILENT, AFTER_UPDATE, ERROR}
}

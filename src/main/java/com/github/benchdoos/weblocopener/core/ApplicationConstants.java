package com.github.benchdoos.weblocopener.core;

import java.io.File;

import static com.github.benchdoos.weblocopenercore.constants.ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME;

public class ApplicationConstants {
    public static String INSTALLER_SILENT_KEY = "/verysilent";
    public static String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + File.separator + WEBLOCOPENER_APPLICATION_NAME + File.separator;

    public static String UPDATER_APPLICATION_NAME = "Updater";

    public static String WINDOWS_SETUP_DEFAULT_NAME = "WeblocOpenerSetup.exe";
    public static String DEBIAN_SETUP_DEFAULT_NAME = "WeblocOpener.deb";
    public static int CONNECTION_TIMEOUT = 5000;
}

package com.github.benchdoos.weblocopener.core;

import java.io.File;

import static com.github.benchdoos.weblocopenercore.constants.ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME;

public interface ApplicationConstants {
    String INSTALLER_SILENT_KEY = "/verysilent";
    String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + File.separator + WEBLOCOPENER_APPLICATION_NAME + File.separator;

    String UPDATER_APPLICATION_NAME = "Updater";

}

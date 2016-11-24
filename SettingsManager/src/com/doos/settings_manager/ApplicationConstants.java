package com.doos.settings_manager;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public interface ApplicationConstants {
    String APP_NAME = "WeblocOpener";
    String APP_VERSION = "1.2.1";
    String APP_LOG_FOLDER_PATH = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator + "Log";
    String APP_LOG_PROPERTY = "WeblocOpener.log.folder";
    String SETTINGS_FILE_PATH = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator + "settings.prop";
    String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator;
    String UPDATE_WEB_URL = "https://github.com/benchdoos/WeblocOpener/";

    boolean APP_AUTO_UPDATE_DEFAULT_VALUE = true;

    String APP_ID = "{F1300E10-BBB2-4695-AC2F-3D58DC0BC0A6}_is1";
    String APP_INSTALL_SILENT_KEY = " /VERYSILENT";
}

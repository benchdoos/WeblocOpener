package com.doos.settings_manager;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public interface ApplicationConstants {
    String APP_NAME = "WeblocOpener";
    String APP_VERSION = "1.2.3";
    String APP_LOG_FOLDER_PATH = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator + "Log";
    String APP_LOG_PROPERTY = "WeblocOpener.log.folder";
    String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator;
    //String UPDATE_WEB_URL = "https://github.com/benchdoos/WeblocOpener/";
    String UPDATE_WEB_URL = "https://benchdoos.github.io/";

    boolean IS_APP_AUTO_UPDATE_DEFAULT_VALUE = true;

    String APP_ID = "{F1300E10-BBB2-4695-AC2F-3D58DC0BC0A6}_is1";
    String APP_INSTALL_SILENT_KEY = " /VERYSILENT";

    String UPDATE_SILENT_ARGUMENT = "-s";
    String UPDATE_DELETE_TEMP_FILE_ARGUMENT = "-clean";

    String OPENER_EDIT_ARGUMENT = "-edit";
    String OPENER_SETTINGS_ARGUMENT = "-settings";
    String OPENER_ABOUT_ARGUMENT = "-about";

}

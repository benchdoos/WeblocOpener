package com.doos.commons.core;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public interface ApplicationConstants {
    String WEBLOC_OPENER_APPLICATION_NAME = "WeblocOpener";
    String UPDATER_APPLICATION_NAME = "Updater";
    String APP_VERSION = "1.4.1";
    String APP_LOG_FOLDER_PATH = System
            .getProperty("java.io.tmpdir") + WEBLOC_OPENER_APPLICATION_NAME + File.separator + "Log";
    String APP_LOG_PROPERTY = "WeblocOpener.log.folder";
    String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + WEBLOC_OPENER_APPLICATION_NAME + File.separator;
    String UPDATE_WEB_URL = "https://benchdoos.github.io/";
    String GITHUB_WEB_URL = "https://github.com/benchdoos/WeblocOpener/";

    String BENCH_DOOS_TELEGRAM_URL = "https://vk.cc/74nB3D"; //for stats


    String BROWSER_DEFAULT_VALUE = "default";

    boolean IS_APP_AUTO_UPDATE_DEFAULT_VALUE = true;

    String APP_ID = "{F1300E10-BBB2-4695-AC2F-3D58DC0BC0A6}_is1";
    String APP_INSTALL_SILENT_KEY = " /VERYSILENT";

    String UPDATE_SILENT_ARGUMENT = "-s";
    String UPDATE_DELETE_TEMP_FILE_ARGUMENT = "-clean";

    String OPENER_CREATE_ARGUMENT = "-create";
    String OPENER_EDIT_ARGUMENT = "-edit";
    String OPENER_SETTINGS_ARGUMENT = "-settings";
    String OPENER_UPDATE_ARGUMENT = "-update";
    String OPENER_ABOUT_ARGUMENT = "-about";
    String OPENER_HELP_ARGUMENT_HYPHEN = "-help";
    String OPENER_HELP_ARGUMENT_SLASH = "/help";

    String WINDOWS_WEBLOCOPENER_SETUP_NAME = "WeblocOpenerSetup";


    int UPDATE_CODE_SUCCESS = 0; //NORMAL state, app updated.
    int UPDATE_CODE_CANCEL = 1; //Install was cancelled or Incorrect function or corrupt file.
    int UPDATE_CODE_NO_FILE = 2; //The system cannot find the file specified. OR! User gave no permissions.
    int UPDATE_CODE_CORRUPT = 193; //Installation file is corrupt.
    int UPDATE_CODE_INTERRUPT = -999; //Downloading/installation was interrupted by user.

    String FILE_LIST_NAME = "browser_list.plist";
    String DEFAULT_LIST_LOCATION = System.getProperty("java.io.tmpdir")
            + WEBLOC_OPENER_APPLICATION_NAME + File.separator + FILE_LIST_NAME;
}

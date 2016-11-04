package com.doos.utils;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public interface ApplicationConstants {
    String APP_NAME = "WeblocOpener";
    String APP_VERSION = "1.2";
    String APP_LOG_FOLDER_PATH = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator + "Log";
    String APP_LOG_PROPERTY = "WeblocOpener.log.folder";
    String SETTINGS_FILE_PATH = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator + "settings.prop";
    String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + APP_NAME + File.separator;
}

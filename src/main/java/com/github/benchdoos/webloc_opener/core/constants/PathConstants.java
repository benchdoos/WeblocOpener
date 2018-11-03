package com.github.benchdoos.webloc_opener.core.constants;

import java.io.File;

import static com.github.benchdoos.webloc_opener.core.constants.ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME;
import static com.github.benchdoos.webloc_opener.core.constants.ApplicationConstants.WINDOWS_WEBLOC_OPENER_SETUP_NAME;

public interface PathConstants {
    String APP_LOG_FOLDER_PATH = System
            .getProperty("java.io.tmpdir") + WEBLOC_OPENER_APPLICATION_NAME + File.separator + "Log";
    String APP_LOG_PROPERTY = "WeblocOpener.log.folder";

    String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + WEBLOC_OPENER_APPLICATION_NAME + File.separator
            + WINDOWS_WEBLOC_OPENER_SETUP_NAME + ".exe";
}

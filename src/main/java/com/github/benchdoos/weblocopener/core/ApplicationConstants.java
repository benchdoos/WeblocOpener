package com.github.benchdoos.weblocopener.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;

import static com.github.benchdoos.weblocopenercore.constants.ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationConstants {
    public static String INSTALLER_SILENT_KEY = "/verysilent";
    public static String UPDATE_PATH_FILE = System.getProperty("java.io.tmpdir") + File.separator + WEBLOCOPENER_APPLICATION_NAME + File.separator;

}

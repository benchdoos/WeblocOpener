package com.github.benchdoos.weblocopener.commons.utils;

import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class Logging {
    public static final File LOG_FOLDER = new File(ApplicationConstants.APP_LOG_FOLDER_PATH);
    private String collingApp;

    public Logging(String collingAppName) {
        collingApp = collingAppName;
        checkFolders();
        startLogging();
    }

    /**
     * Returns the name of called class. Made for usage in static methods.
     * Created to minimize hardcoded code.
     *
     * @return a name of called class.
     */
    public static String getCurrentClassName() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            return e.getStackTrace()[1].getClassName();
        }
    }

    /**
     * Checks folders for Log4j, creates them, if needed.
     */
    private void checkFolders() {

        File infoLogFolder = new File(ApplicationConstants.APP_LOG_FOLDER_PATH + File.separator + collingApp
                + File.separator + "INFO");
        File warnLogFolder = new File(ApplicationConstants.APP_LOG_FOLDER_PATH + File.separator + collingApp
                + File.separator + "WARN");
        File debugLogFolder = new File(ApplicationConstants.APP_LOG_FOLDER_PATH + File.separator + collingApp
                + File.separator + "DEBUG");
        if (!LOG_FOLDER.exists()) {
            boolean success = LOG_FOLDER.mkdirs();
            if (!success) {
                System.out.println("Could not create file: " + LOG_FOLDER.getAbsolutePath());
            }
        }

        if (!infoLogFolder.exists()) {
            boolean success = infoLogFolder.mkdirs();
            if (!success) {
                System.out.println("Could not create file: " + infoLogFolder.getAbsolutePath());
            }
        }

        if (!warnLogFolder.exists()) {
            boolean success = warnLogFolder.mkdirs();
            if (!success) {
                System.out.println("Could not create file: " + warnLogFolder.getAbsolutePath());
            }
        }
        if (!debugLogFolder.exists()) {
            boolean success = debugLogFolder.mkdirs();
            if (!success) {
                System.out.println("Could not create file: " + debugLogFolder.getAbsolutePath());
            }
        }

    }

    /**
     * Creates a property for Log4j
     * Warning! Run this before any log implementation.
     */
    private void startLogging() {
        final String path = ApplicationConstants.APP_LOG_FOLDER_PATH + File.separator + collingApp;
        System.setProperty(ApplicationConstants.APP_LOG_PROPERTY, path);
        System.out.println("Logging starts at: " + path);
    }

}

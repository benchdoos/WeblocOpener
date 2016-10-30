package com.doos.Service;

import com.doos.ApplicationConstants;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class Logging {
    public Logging() {
        checkFolders();
        startLogging();
    }

    private void checkFolders() {
        File logFolder = new File(ApplicationConstants.AppLogFolderPath);
        File infoLogFolder = new File(ApplicationConstants.AppLogFolderPath + File.separator + "INFO");
        File warnLogFolder = new File(ApplicationConstants.AppLogFolderPath + File.separator + "WARN");
        File debugLogFolder = new File(ApplicationConstants.AppLogFolderPath + File.separator + "DEBUG");
        if (!logFolder.exists()) {
            boolean success = logFolder.mkdirs();
            if (!success) {
                System.out.println("Could not create file: " + logFolder.getAbsolutePath());
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

    private void startLogging() {
        System.setProperty(ApplicationConstants.AppLogProperty, ApplicationConstants.AppLogFolderPath);
        System.out.println("Logging starts at: " + ApplicationConstants.AppLogFolderPath);
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

}

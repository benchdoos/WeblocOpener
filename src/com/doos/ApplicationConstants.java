package com.doos;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public interface ApplicationConstants {
    String AppName = "WeblocOpener";
    String AppVersion = "1.0";
    String AppLogFolderPath = System.getProperty("java.io.tmpdir") + AppName + File.separator + "Log";
    String AppLogProperty = "WeblocOpener.log.folder";
}

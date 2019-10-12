/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.utils;

import com.github.benchdoos.weblocopenercore.core.constants.PathConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class Logging {
    private final String collingApp;
    public static final File LOG_FOLDER = new File(PathConstants.APP_LOG_FOLDER_PATH);


    public Logging(String collingAppName) {
        collingApp = collingAppName;
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
     * Creates a property for Log4j
     * Warning! Run this before any log implementation.
     */
    private void startLogging() {
        final String path = PathConstants.APP_LOG_FOLDER_PATH + File.separator + collingApp + File.separator;
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        System.out.println("Logging starts at: " + path);
        System.setProperty(PathConstants.APP_LOG_PROPERTY, path);
        final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
        log.info("Logging successfully started. Welcome to {}", CoreUtils.getApplicationVersionFullInformationString());

    }

}

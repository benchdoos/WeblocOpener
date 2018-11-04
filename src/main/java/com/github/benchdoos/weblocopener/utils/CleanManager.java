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

import com.github.benchdoos.weblocopener.core.constants.StringConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

import static com.github.benchdoos.weblocopener.core.constants.PathConstants.UPDATE_PATH_FILE;

public class CleanManager {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static void clean() {
        File folder = new File(UPDATE_PATH_FILE);
        log.info("Cleaning: {}", folder);
        if (folder.isDirectory()) {
            final File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().contains(StringConstants.WINDOWS_WEBLOCOPENER_SETUP_NAME)) {
                        final boolean delete = file.delete();
                        log.info("Setup file was deleted ({}): {}", file, delete);
                    }
                }
            }
        }
    }
}

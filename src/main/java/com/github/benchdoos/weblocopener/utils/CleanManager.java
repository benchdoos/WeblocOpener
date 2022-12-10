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

import com.github.benchdoos.weblocopener.core.ApplicationConstants;
import lombok.extern.log4j.Log4j2;

import java.io.File;

import static com.github.benchdoos.weblocopener.core.ApplicationConstants.UPDATE_PATH_FILE;


@Log4j2
public class CleanManager {
    public static void clean() {
        File folder = new File(UPDATE_PATH_FILE);
        log.info("Cleaning: {}", folder);
        if (folder.isDirectory()) {
            final File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    final boolean windows = file.getName().contains(ApplicationConstants.WINDOWS_SETUP_DEFAULT_NAME);
                    final boolean debian = file.getName().contains(ApplicationConstants.DEBIAN_SETUP_DEFAULT_NAME);
                    if (windows || debian) {
                        final boolean delete = file.delete();
                        log.info("Setup file was deleted ({}): {}", file, delete);
                    }
                }
            }
        }
    }
}

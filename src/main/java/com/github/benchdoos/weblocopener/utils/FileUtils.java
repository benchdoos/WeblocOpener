/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
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

import com.github.benchdoos.weblocopener.utils.system.OperatingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static void openFileInFileBrowser(File file) {
        Thread thread = new Thread(() -> openFile(file));
        thread.start();
    }

    private static void openFile(File file) {
        log.info("Opening file in system file manager: {}", file);
        try {
            log.debug("Current system is: {}", OperatingSystem.getOsName());
            if (OperatingSystem.isWindows()) {
                openFileInWindowsExplorer(file);
            } else if (OperatingSystem.isUnix()) {
                openFileInNautilusUnix(file);
            }
        } catch (IOException ex) {
            log.warn("Could not open file {} in explorer", file, ex);
            try {
                log.debug("Opening parent: {}", file.getParentFile());
                Desktop.getDesktop().open(file.getParentFile());
            } catch (Exception ex1) {
                log.warn("Could not open parent for file: {}, skipping.", file, ex1);
            }
        }
    }

    private static void openFileInNautilusUnix(File file) throws IOException {
        if (file.getAbsolutePath().contains(" ")) {
            throw new IOException("Path contains spaces, so...");
        }
        log.debug("Opening {} in nautilus", file);
        Runtime.getRuntime().exec("nautilus \'" + file + "\'");
    }

    private static void openFileInWindowsExplorer(File file) throws IOException {
        log.debug("Opening {} in Explorer", file);
        Runtime.getRuntime().exec("explorer.exe /select,\"" + file + "\"");
    }
}

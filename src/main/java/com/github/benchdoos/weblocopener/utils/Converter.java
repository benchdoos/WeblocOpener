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

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.service.Analyzer;
import com.github.benchdoos.weblocopener.service.UrlsProceed;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static com.github.benchdoos.weblocopener.core.constants.ApplicationConstants.URL_FILE_EXTENSION;
import static com.github.benchdoos.weblocopener.core.constants.ApplicationConstants.WEBLOC_FILE_EXTENSION;

public class Converter {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static File convertUrlToWebloc(File originalUrlFile) throws IOException {
        validateFile(originalUrlFile, ApplicationConstants.URL_FILE_EXTENSION);

        final URL internetShortcut = InternetShortcut.getInternetShortcut(originalUrlFile);

        log.debug("Shortcut for file [{}] url is: ", originalUrlFile, internetShortcut);

        if (internetShortcut == null) {
            throw new NullPointerException("Url in internet shortcut [" + originalUrlFile + "] is null");
        }

        File file = prepareNewFile(originalUrlFile, WEBLOC_FILE_EXTENSION);
        if (!file.exists()) {
            UrlsProceed.createWebloc(file.getAbsolutePath(), internetShortcut);
            return file;
        } else throw new FileExistsException("File [" + file + "] already exists");

    }

    public static File convertWeblocToUrl(File originalWeblocFile) throws Exception {
        validateFile(originalWeblocFile, WEBLOC_FILE_EXTENSION);
        Analyzer analyzer = new Analyzer(originalWeblocFile.getAbsolutePath());
        final URL url = new URL(analyzer.getUrl());

        File file = prepareNewFile(originalWeblocFile, URL_FILE_EXTENSION);
        if (!file.exists()) {
            InternetShortcut.createInternetShortcut(file, url.toString(), null);
            return file;
        } else throw new FileExistsException("File [" + file + "] already exists");
    }

    private static File prepareNewFile(File originalWeblocFile, String urlFileExtension) {
        String filename = FilenameUtils.removeExtension(originalWeblocFile.getName());

        String folder = originalWeblocFile.getParentFile().getAbsolutePath();

        final String filePath = folder + File.separator + filename + "." + urlFileExtension;
        return new File(filePath);
    }

    private static void validateFile(File originalUrlFile, String extension) throws FileNotFoundException {
        if (originalUrlFile == null) {
            throw new IllegalArgumentException("Original file can not be null");
        }
        if (!originalUrlFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + originalUrlFile);
        }
        if (!Analyzer.getFileExtension(originalUrlFile).equalsIgnoreCase(extension)) {
            throw new IllegalArgumentException("File extension is not *.url: " + originalUrlFile);
        }
    }
}

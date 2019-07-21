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

import com.github.benchdoos.weblocopener.service.links.Link;
import com.github.benchdoos.weblocopener.service.links.LinkFactory;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class Converter {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static File convert(File originalFile, Link convertedLink) throws Exception {
        log.debug("Converting file: {} to new extension: {}", originalFile, convertedLink.getExtension());
        validateFile(originalFile, convertedLink);

        Link originalLink = new LinkFactory().getLink(originalFile);
        final URL originalUrl = originalLink.getUrl(originalFile);

        File file = prepareNewFile(originalFile, convertedLink.getExtension());
        if (!file.exists()) {
            convertedLink.createLink(file, originalUrl);
            return file;
        } else throw new FileExistsException("File [" + file + "] already exists");
    }

    private static File prepareNewFile(File originalWeblocFile, String urlFileExtension) {
        String filename = FilenameUtils.removeExtension(originalWeblocFile.getName());

        String folder = originalWeblocFile.getParentFile().getAbsolutePath();

        final String filePath = folder + File.separator + filename + "." + urlFileExtension;
        return new File(filePath);
    }

    private static void validateFile(File originalUrlFile, Link link) throws FileNotFoundException {
        final String extension = link.getExtension();

        if (originalUrlFile == null) {
            throw new IllegalArgumentException("Original file can not be null");
        }
        if (!originalUrlFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + originalUrlFile);
        }
        if (FileUtils.getFileExtension(originalUrlFile).equalsIgnoreCase(extension)) {
            throw new IllegalArgumentException("File extension " + originalUrlFile + " equals to new extension " + extension);
        }
    }
}

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

import com.github.benchdoos.linksupport.links.Link;
import com.github.benchdoos.weblocopener.service.links.LinkUtilities;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

@Log4j2
public class Converter {
    public static File convert(final File originalFile, final Link convertedLink) throws Exception {
        log.debug("Converting file: {} to new extension: {}", originalFile, convertedLink.getExtension());
        validateFile(originalFile, convertedLink);

        final Link originalLink = LinkUtilities.getByFilePath(originalFile.getAbsolutePath());
        if (originalLink == null) {
            throw new IllegalArgumentException("Could not get link by extension for file: [" + originalFile + "]");
        }
        final URL originalUrl =  originalLink.getLinkProcessor().getUrl(new FileInputStream(originalFile));

        final File file = prepareNewFile(originalFile, convertedLink.getExtension());
        if (!file.exists()) {
            convertedLink.getLinkProcessor().createLink(originalUrl, new FileOutputStream(file));
            return file;
        } else throw new FileExistsException("File [" + file + "] already exists");
    }

    private static File prepareNewFile(final File originalWeblocFile, final String urlFileExtension) {
        final String filename = FilenameUtils.removeExtension(originalWeblocFile.getName());

        final String folder = originalWeblocFile.getParentFile().getAbsolutePath();

        final String filePath = folder + File.separator + filename + "." + urlFileExtension;
        return new File(filePath);
    }

    private static void validateFile(final File originalUrlFile, final Link link) throws FileNotFoundException {
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

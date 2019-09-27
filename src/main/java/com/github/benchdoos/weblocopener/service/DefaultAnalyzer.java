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

package com.github.benchdoos.weblocopener.service;

import com.github.benchdoos.jcolorful.core.JColorful;
import com.github.benchdoos.linksupport.links.Link;
import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.gui.FileChooser;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.service.links.LinkUtilities;
import com.github.benchdoos.weblocopener.utils.FileUtils;
import com.github.benchdoos.weblocopener.utils.FrameUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.github.benchdoos.weblocopener.core.constants.ApplicationConstants.WEBLOC_FILE_EXTENSION;

@Log4j2
public class DefaultAnalyzer {
    private String url = "";
    private File selectedFile = null;

    public DefaultAnalyzer(final String filePath) throws Exception {
        log.debug("Starting analyze");
        log.debug("Got argument: " + filePath);

        if (filePath == null) {
            throw new IllegalArgumentException("Filepath can not be null.");
        }

        analyzeFile(filePath);
    }

    private void analyzeFile(final String filePath) throws Exception {
        final File file = new File(filePath);
        if (file.exists()) {
            if (LinkUtilities.isFileSupported(FileUtils.getFileExtension(file))) {
                selectedFile = file;
            }
        } else {
            final ArrayList<File> weblocFiles = getWeblocFiles(filePath);

            File chosen = null;
            if (weblocFiles != null) {
                if (weblocFiles.size() > 1) {
                    final FileChooser fileChooser;
                    if (PreferencesManager.isDarkModeEnabledNow()) {
                        final JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
                        colorful.colorizeGlobal();
                        fileChooser = new FileChooser(weblocFiles);
                        SwingUtilities.invokeLater(() -> colorful.colorize(fileChooser));
                    } else {
                        fileChooser = new FileChooser(weblocFiles);
                    }
                    fileChooser.setLocation(FrameUtils.getFrameOnCenterLocationPoint(fileChooser));
                    fileChooser.setVisible(true);
                    chosen = fileChooser.getChosenFile();
                } else if (weblocFiles.size() == 1) {
                    chosen = weblocFiles.get(0);
                } else {
                    log.warn("Could not find files that math... or something went wrong: {}", weblocFiles);
                }
            }

            if (chosen != null) {
                if (chosen.exists()) {
                    selectedFile = chosen;
                }
            } else {
                log.debug("User canceled selection from file list");
            }
        }

        if (selectedFile != null) {
            try {
                final Link byFilePath = LinkUtilities.getByFilePath(selectedFile.getAbsolutePath());
                url = byFilePath.getLinkProcessor()
                        .getUrl(new FileInputStream(selectedFile)).toString();
            } catch (IOException e) {
                log.warn("Could not parse Url for selected file: {}", selectedFile, e);
                log.info("Trying to get url from selected file: {}", selectedFile);
            }
        } else {
            throw new FileNotFoundException("Can not analyze file: " + filePath);
        }
    }

    private int compareFileNames(final File fileOriginal, final File fileComparing) {
        final String originalFileName = fileOriginal.getName();
        final String comparingFileName = fileComparing.getName();
        return FuzzySearch.ratio(originalFileName, comparingFileName);
    }

    private ArrayList<File> findOpeningFile(final File file) {
        final File parent = file.getParentFile();
        if (parent.exists() && parent.isDirectory()) {
            final ArrayList<ComparedFile> values = new ArrayList<>();

            final File[] files = parent.listFiles();
            if (files != null) {
                for (File current : files) {
                    if (FileUtils.getFileExtension(current).equalsIgnoreCase(ApplicationConstants.WEBLOC_FILE_EXTENSION)) {
                        int compared = compareFileNames(file, current);
                        values.add(new ComparedFile(compared, current));
                    }
                }
                final int maximumValue = getMaximumValue(values);

                return getAllFilesWithMaximumValue(maximumValue, values);
            }
        }
        return null;
    }

    private ArrayList<File> getAllFilesWithMaximumValue(final int maximumValue, final ArrayList<ComparedFile> values) {
        ArrayList<File> result = null;
        for (ComparedFile c : values) {
            if (c.getEqualSymbols() == maximumValue) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(c.getFile());
            }
        }
        return result;
    }

    public File getFile() {
        return selectedFile;
    }

    private int getMaximumValue(final ArrayList<ComparedFile> values) {
        return values.stream()
                .mapToInt(ComparedFile::getEqualSymbols)
                .filter(comparedFile -> comparedFile >= 0)
                .max().orElse(0);
    }

    public String getUrl() {
        return url;
    }

    /**
     * Returns a <code>.webloc</code> file from path.
     *
     * @param arg File path.
     * @return <code>.webloc</code> file.
     */
    private ArrayList<File> getWeblocFiles(final String arg) {

        final File currentFile = new File(arg);
        log.info("File [" + arg + "] exists: " + currentFile.exists() + " file?: " + currentFile.isFile());
        if (currentFile.isFile() && currentFile.exists()) {
            if (FilenameUtils.getExtension(currentFile.getName()).equals(WEBLOC_FILE_EXTENSION)) {
                log.info("File added to proceed: " + currentFile.getAbsolutePath());
                final ArrayList<File> arrayList = new ArrayList<>();
                arrayList.add(currentFile);
                return arrayList;
            } else {
                log.warn("Wrong argument. File extension is not webloc: " + currentFile.getAbsolutePath());
            }
        } else {
            log.warn("Wrong argument. Invalid file path or file not exist: " + currentFile.getAbsolutePath());
            log.info("Trying to guess what file user wants to open");
            final ArrayList<File> files = findOpeningFile(currentFile);
            log.info("Got files, that match: {}", files);
            return files;
        }
        return null;
    }

    @AllArgsConstructor
    @Data
    private static class ComparedFile {
        private int equalSymbols;
        private File file;
    }
}

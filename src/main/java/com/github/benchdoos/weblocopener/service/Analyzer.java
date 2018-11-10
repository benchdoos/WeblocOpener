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

package com.github.benchdoos.weblocopener.service;

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.gui.EditDialog;
import com.github.benchdoos.weblocopener.gui.FileChooser;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.UserUtils;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.github.benchdoos.weblocopener.core.constants.ApplicationConstants.WEBLOC_FILE_EXTENSION;


public class Analyzer {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());


    private String url = "";

    public Analyzer(String filePath) {
        log.debug("Starting analyze");
        log.debug("Got argument: " + filePath);

        if (filePath == null) {
            throw new IllegalArgumentException("Argument can not be null.");
        }

        analyzeFile(filePath);


    }

    private void analyzeFile(String filePath) {
        ArrayList<File> files = null;
        File chosenFile = null;
        try {
            files = getWeblocFile(filePath);
            String url;
            if (files != null) {
                if (files.size() == 1) {
                    chosenFile = files.get(0);
                    url = UrlsProceed.takeUrl(chosenFile);
                    if (url.isEmpty()) {
                        throw new NullPointerException("Url is empty, just editing");
                    } else {
                        this.url = url;
                    }
                } else {
                    FileChooser fileChooser = new FileChooser(files);
                    fileChooser.setVisible(true);
                    chosenFile = fileChooser.getChosenFile();
                    log.debug("Chosen file is: {}", chosenFile);
                    if (chosenFile != null) {
                        url = UrlsProceed.takeUrl(chosenFile);
                        if (url.isEmpty()) {
                            new EditDialog(chosenFile.getAbsolutePath()).setVisible(true);
                        } else {
                            this.url = url;
                        }
                    }
                }
            }

        } catch (NullPointerException e) {
            log.warn("URL in files [" + files + "] has empty link.", e);
            editFile(files);
        } catch (IOException e) {
            String message = "Can not read files [" + files + "]";
            log.warn(message, e);
            UserUtils.showErrorMessageToUser(null, "Error", message);
        } catch (Exception e) {
            final String message = "URL in files [" + files + "] is corrupt.";
            log.warn(message, e);
            UserUtils.showErrorMessageToUser(null, "Error", message);

            if (chosenFile != null) {
                new EditDialog(chosenFile.getAbsolutePath()).setVisible(true);
            }
        }
    }

    private int compareFileNames(File fileOriginal, File fileComparing) {
        final String originalFileName = fileOriginal.getName();
        final String comparingFileName = fileComparing.getName();
        return FuzzySearch.ratio(originalFileName, comparingFileName);
    }

    private void editFile(ArrayList<File> files) {
        if (files != null) {
            if (files.size() == 1) {
                new EditDialog(files.get(0).getAbsolutePath()).setVisible(true);
            } else {
                FileChooser fileChooser = new FileChooser(files);
                fileChooser.setVisible(true);
                final File chosenFile = fileChooser.getChosenFile();
                if (chosenFile != null) {
                    new EditDialog(chosenFile.getAbsolutePath()).setVisible(true);
                }
            }
        }
    }

    private ArrayList<File> findOpeningFile(File currentFile) {
        File parent = currentFile.getParentFile();
        if (parent.exists() && parent.isDirectory()) {
            ArrayList<ComparedFile> values = new ArrayList<>();

            final File[] files = parent.listFiles();
            if (files != null) {
                for (File current : files) {
                    if (getFileExtension(current).equalsIgnoreCase(ApplicationConstants.WEBLOC_FILE_EXTENSION)) {
                        int compared = compareFileNames(currentFile, current);
                        values.add(new ComparedFile(compared, current));
                    }
                }

                final int maximumValue = getMaximumValue(values);

                return getAllFilesWithMaximumValue(maximumValue, values);
            }
        }
        return null;
    }

    private ArrayList<File> getAllFilesWithMaximumValue(int maximumValue, ArrayList<ComparedFile> values) {
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

    /**
     * Returns file extension
     * Examples:
     * <blockquote><pre>
     * "hello.exe" returns "exe"
     * "picture.gif" returns "gif"
     * "document.txt" returns "txt"
     * </pre></blockquote>
     *
     * @param file to get extension.
     * @return String name of file extension
     */
    private String getFileExtension(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File can not be null");
        }
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private int getMaximumValue(ArrayList<ComparedFile> values) {
        int result = 0;
        for (ComparedFile comparedFile : values) {
            if (comparedFile.getEqualSymbols() > result) {
                result = comparedFile.getEqualSymbols();
            }
        }
        return result;
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
    private ArrayList<File> getWeblocFile(String arg) {

        File currentFile = new File(arg);
        log.info("File [" + arg + "] exists: " + currentFile.exists() + " file?: " + currentFile.isFile());
        if (currentFile.isFile() && currentFile.exists()) {
            if (getFileExtension(currentFile).equals(WEBLOC_FILE_EXTENSION)) {
                log.info("File added to proceed: " + currentFile.getAbsolutePath());
                ArrayList<File> arrayList = new ArrayList<>();
                arrayList.add(currentFile);
                return arrayList;
            } else {
                log.warn("Wrong argument. File extension is not webloc: " + currentFile.getAbsolutePath());
            }
        } else {
            log.warn("Wrong argument. Invalid file path or file not exist: " + currentFile.getAbsolutePath());
            log.info("Trying to guess what file user wants to open");
            ArrayList<File> files = findOpeningFile(currentFile);
            log.info("Got files, that match: {}", files);
            return files;
        }

        return null;
    }

    private class ComparedFile {
        private int equalSymbols;
        private File file;

        public ComparedFile(int equalSymbols, File file) {
            this.equalSymbols = equalSymbols;
            this.file = file;
        }

        public int getEqualSymbols() {
            return equalSymbols;
        }

        public ComparedFile setEqualSymbols(int equalSymbols) {
            this.equalSymbols = equalSymbols;
            return this;
        }

        public File getFile() {
            return file;
        }

        public ComparedFile setFile(File file) {
            this.file = file;
            return this;
        }

        @Override
        public String toString() {
            return "ComparedFile{" + "equalSymbols=" + equalSymbols +
                    ", file=" + file +
                    '}';
        }
    }
}

package com.github.benchdoos.weblocopener.weblocOpener.service;

import com.github.benchdoos.weblocopener.commons.utils.UserUtils;
import com.github.benchdoos.weblocopener.weblocOpener.gui.EditDialog;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import static com.github.benchdoos.weblocopener.commons.utils.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class Analyzer {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

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
        File file = null;
        try {
            file = getWeblocFile(filePath);
            String url;
            url = UrlsProceed.takeUrl(file);
            if (url.isEmpty()) {
                throw new NullPointerException("Url is empty, just editing");
            }
            this.url = url;
        } catch (NullPointerException e) {
            log.warn("URL in file [" + file + "] has empty link.", e);
            if (file != null) {
                new EditDialog(file.getAbsolutePath()).setVisible(true);
            }
        } catch (IOException e) {
            String message = "Can not read file [" + file + "]";
            log.warn(message, e);
            UserUtils.showErrorMessageToUser(null, "Error", message);
        } catch (Exception e) {
            final String message = "URL in file [" + file + "] is corrupt.";
            log.warn(message, e);
            UserUtils.showErrorMessageToUser(null, "Error", message);
            if (file != null) {
                new EditDialog(file.getAbsolutePath()).setVisible(true);
            }
        }
    }


    /**
     * Returns a <code>.webloc</code> file from path.
     *
     * @param arg File path.
     * @return <code>.webloc</code> file.
     */
    private File getWeblocFile(String arg) {

        File currentFile = new File(arg);
        if (currentFile.isFile() && currentFile.exists()) {
            if (getFileExtension(currentFile).equals("webloc")) {
                log.info("File added to proceed: " + currentFile.getAbsolutePath());
                return currentFile;
            } else {
                log.warn("Wrong argument. File extension is not webloc: " + currentFile.getAbsolutePath());
            }
        } else {
            log.warn("Wrong argument. Invalid file path or file not exist: " + currentFile.getAbsolutePath());
        }

        return null;
    }

    public String getUrl() {
        return url;
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
}

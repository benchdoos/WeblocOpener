package com.doos.service;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.doos.gui.EditDialog;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.doos.service.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class Analyzer {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    private String url = "";

    public Analyzer(String arg) {
        log.debug("Starting analyze");
        log.debug("Got argument: " + arg);

        File file = getWeblocFile(arg);
        String url = "";
        try {
            url = takeUrl(file);
            if (url.equals("")) {
                throw new NullPointerException("Url is empty, just editing");
            }
            this.url = url;
        } catch (NullPointerException e) {
            log.warn("URL in file [" + file + "] has empty link.", e);
            assert file != null;
            new EditDialog(file.getAbsolutePath()).setVisible(true);
        } catch (Exception e) {
            log.warn("URL in file [" + file + "] is corrupt.", e);
            JOptionPane.showMessageDialog(new Frame(), "URL in file [" + file + "] is corrupt.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            assert file != null;
            new EditDialog(file.getAbsolutePath()).setVisible(true);
        }


    }


    /**
     * Takes URL from <code>.webloc</code> file
     *
     * @param file File <code>.webloc</code>
     * @return String - URL in file
     * @see java.io.File
     */
    public static String takeUrl(File file) throws Exception {

        log.debug("Got file: " + file.getAbsolutePath());
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(file);
        String url = rootDict.objectForKey("URL").toString();
        log.info("Got url: " + url);

        return url;
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
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}

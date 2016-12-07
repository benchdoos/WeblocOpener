package com.doos.webloc_opener.service;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.doos.commons.utils.Logging;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class UrlsProceed {
    private static final Logger log = Logger.getLogger(Logging.getCurrentClassName());


    /**
     * Opens url on default browser.
     *
     * @param url Url to open.
     */
    public static void openUrl(String url) {
        if (!Desktop.isDesktopSupported()) {
            log.warn("Desktop is not supported");
            return;
        }
        Desktop desktop = Desktop.getDesktop();

        try {
            if (!url.isEmpty()) {
                desktop.browse(URI.create(url));
            }
        } catch (IOException e) {
            log.warn("Can not open url: " + url, e);
            JOptionPane.showMessageDialog(null, "URL is corrupt: " + url);
        }

    }


    /**
     * Log before program shutdown.
     */
    public static void shutdownLogout() {
        log.debug("Goodbye!");
    }


    /**
     * Creates <code>.webloc</code> file on given path.
     *
     * @param url  URL to create.
     * @param path Path of creating file.
     */
    public static void createWebloc(URL url, String path) {
        log.info("Creating .webloc at [" + path + "] URL: [" + url + "]");
        NSDictionary root = new NSDictionary();
        root.put("URL", url.toString());


        try {
            File file = new File(path);
            PropertyListParser.saveAsXML(root, file);
        } catch (IOException e) {
            log.warn("Can not create .webloc file", e);
        }
    }

    /**
     * Takes URL from <code>.webloc</code> file
     *
     * @param file File <code>.webloc</code>
     * @return String - URL in file
     * @see File
     */
    public static String takeUrl(File file) throws Exception {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist, path: " + file);
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("File can not be a directory, path: " + file);
        }

        log.debug("Got file: " + file);
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(file);
        String url = rootDict.objectForKey("URL").toString();
        log.info("Got url: [" + url + "] from file: " + file);

        return url;
    }
}

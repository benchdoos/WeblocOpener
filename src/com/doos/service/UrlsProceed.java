package com.doos.service;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import static com.doos.service.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class UrlsProceed {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    public static void openUrls(ArrayList<String> urls) {
        if (!Desktop.isDesktopSupported()) {
            log.warn("Desktop is not supported");
            return;
        }
        Desktop desktop = Desktop.getDesktop();


        log.debug("Got urls to open: " + urls.size());

        for (String url : urls) {
            try {
                desktop.browse(URI.create(url));
            } catch (IOException e) {
                log.warn("Can not open url: " + url, e);
                JOptionPane.showMessageDialog(new Frame(), "URL is corrupt: " + url);
            }
        }
    }

    public static void shutdownLogout() {
        log.debug("Goodbye!");
    }

    public static void createWebloc(URL url, String path) {
        log.info("Creating .webloc at [" + path + "] URL: [" + url + "]");
        NSDictionary root = new NSDictionary();
        root.put("URL", url.toString());


        try {
            File file = new File(path);
            //File file = new File(location + "untitled.webloc");
            //TODO uncomment
            /*if (file.exists()) {
                for (int i = 1; i < Integer.MAX_VALUE; i++) {
                    if (file.exists()) {
                        file = new File(location + "untitled (" + i + ").webloc");
                    } else i = Integer.MAX_VALUE-1;
                }
            }*/

            PropertyListParser.saveAsXML(root, file);
        } catch (IOException e) {
            log.warn("Can not create .webloc file", e);
        }
    }
}

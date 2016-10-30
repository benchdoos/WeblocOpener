package com.doos.Service;

import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import static com.doos.Service.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class UrlsProceed {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    public static void openUrls(ArrayList<String> urls) {
        if(!Desktop.isDesktopSupported()){
            log.warn("Desktop is not supported");
            return;
        }
        Desktop desktop = Desktop.getDesktop();


        log.debug("Got urls to open: " + urls.size());

        for (String url: urls) {
            try {
                desktop.browse(URI.create(url));
            } catch (IOException e) {
                log.warn("Can not open url: " + url, e);
            }
        }
    }

    public static void shutdownLogout() {
        log.debug("Goodbye!");
    }
}

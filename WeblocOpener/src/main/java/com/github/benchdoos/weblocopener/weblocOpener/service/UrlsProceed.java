package com.github.benchdoos.weblocopener.weblocOpener.service;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.registry.RegistryManager;
import com.github.benchdoos.weblocopener.commons.utils.Logging;
import com.github.benchdoos.weblocopener.commons.utils.UserUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class UrlsProceed {
    public static final int QR_CODE_HEIGHT = 300, QR_CODE_WIDTH = 300;
    private static final Logger log = Logger.getLogger(Logging.getCurrentClassName());
    private static final String QR_CODE_CHARSET = "UTF-8";


    /**
     * Opens url on default browser.
     *
     * @param url Url to open.
     */
    public static void openUrl(String url) {
        if (RegistryManager.getBrowserValue().equals(ApplicationConstants.BROWSER_DEFAULT_VALUE)
                || RegistryManager.getBrowserValue().isEmpty()) {
            log.info("Opening URL in default browser: " + url);
            openUrlInDefaultBrowser(url);
        } else {
            try {
                log.info("Opening URL in not default browser with call:[" + RegistryManager.getBrowserValue() + "]: " + url);
                openUrlInNotDefaultBrowser(url);
            } catch (IOException e) {
                log.warn(e);
            }
        }

    }

    public static void openUrl(URL url) {
        openUrl(url.toString());
    }

    private static void openUrlInNotDefaultBrowser(String url) throws IOException {
        if (!url.isEmpty()) {
            String call = RegistryManager.getBrowserValue().replace("%site", url);
            Runtime runtime = Runtime.getRuntime();
            final String command = "cmd /c " + call;
            if (call.startsWith("start")) {
                Process process = runtime.exec(command);

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(process.getErrorStream()));

                // read the output from the command
                String errorMessage = null;
                boolean error = false;
                while ((errorMessage = stdError.readLine()) != null) {
                    error = true;
                    log.warn("Can not start this browser: " + errorMessage);
                    log.info("Opening in default browser: " + url);
                }
                if (error) {
                    openUrlInDefaultBrowser(url);
                }
            } else {
                runtime.exec(call);
            }

        }
    }

    private static void openUrlInDefaultBrowser(String url) {
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
            UserUtils.showWarningMessageToUser(null, null, "URL is corrupt: " + url);
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
    public static void createWebloc(String path, URL url) {
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
        log.info("Got URL: [" + url + "] from file: " + file);

        return url;
    }

    public static BufferedImage generateQrCode(String url) throws IOException, WriterException {
        Map hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.MARGIN, 1);
        return createQRCode(url, QR_CODE_CHARSET, hintMap, QR_CODE_HEIGHT, QR_CODE_WIDTH);
    }

    private static BufferedImage createQRCode(String url, String charset,
                                              Map hintMap, int height, int width)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(url.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, width, height, hintMap);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}

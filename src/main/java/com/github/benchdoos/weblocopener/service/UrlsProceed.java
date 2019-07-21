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

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.QrCodeUtils;
import com.github.benchdoos.weblocopener.utils.notification.NotificationManager;
import com.github.benchdoos.weblocopener.utils.system.OperatingSystem;
import com.github.benchdoos.weblocopener.utils.system.SystemUtils;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class UrlsProceed {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public static BufferedImage generateQrCode(String url) throws IOException, WriterException {
        final MatrixToImageConfig conf;
        if (PreferencesManager.isDarkModeEnabledNow()) {
            conf = new MatrixToImageConfig(Color.WHITE.getRGB(), Color.BLACK.getRGB());
        } else {
            conf = new MatrixToImageConfig(Color.BLACK.getRGB(), Color.WHITE.getRGB());
        }
        return QrCodeUtils.generateQrCode(url, conf);
    }

    public static void openUrl(URL url) {
        openUrl(url.toString());
    }

    /**
     * Opens url on default browser.
     *
     * @param url Url to open.
     */
    public static void openUrl(String url) {
        if (PreferencesManager.getBrowserValue().equals(SettingsConstants.BROWSER_DEFAULT_VALUE)
                || PreferencesManager.getBrowserValue().isEmpty()) {
            log.info("Opening URL in default browser: " + url);
            openUrlInDefaultBrowser(url);
        } else {
            try {
                log.info("Opening URL in not default browser with call:[" + PreferencesManager.getBrowserValue() + "]: " + url);
                openUrlInNotDefaultBrowser(url);
            } catch (IOException e) {
                log.warn("Could not open url in not default browser", e);
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
            NotificationManager
                    .getForcedNotification(null).showErrorNotification(
                    null, Translation.getTranslatedString(
                            "CommonsBundle", "urlIsCorruptMessage") + url);
        }
    }

    private static void openUrlInNotDefaultBrowser(String url) throws IOException {
        final OperatingSystem.OS currentOS = SystemUtils.getCurrentOS();

        switch (currentOS) {
            case WINDOWS:
                openUrlOnWindows(url);
                break;
            case UNIX:
                openUrlOnUnix(url);
                break;
            default:
                log.warn("Could not open url on current system: {}", currentOS);
                break;
        }
    }

    private static void openUrlOnUnix(String url) throws IOException {
        if (!url.isEmpty()) {
            String call = PreferencesManager.getBrowserValue().replace("%site", url);
            String[] preparedCall = call.split(" ");
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(preparedCall);
        }
    }

    private static void openUrlOnWindows(String url) throws IOException {
        if (!url.isEmpty()) {
            String call = PreferencesManager.getBrowserValue().replace("%site", url);
            Runtime runtime = Runtime.getRuntime();
            final String command = "cmd /c " + call;
            if (call.startsWith("start")) {
                Process process = runtime.exec(command);

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(process.getErrorStream()));

                // read the output from the command
                String errorMessage;
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


}

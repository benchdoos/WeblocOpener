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

package com.github.benchdoos.weblocopener.utils.notification.impl;

import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.notification.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UnixNotification implements Notification {
    public static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    private static final int TIME_NOTIFICATION_DELAY = 50_000; //5 secs

    private static void createNotification(String title, String message, TrayIcon.MessageType messageType) {

        title = prepareString(title);
        message = prepareString(message);

        URI uri = getImageUriForMessageType(messageType);

        showNotification(title, message, uri);
    }

    private static String prepareString(String string) {
        return string == null ? "" : string;
    }

    private static URI getImageUriForMessageType(TrayIcon.MessageType messageType) {
        URI uri = null;
        try {
            switch (messageType) {
                case WARNING: {
                    uri = UnixNotification.class.getResource("/images/notification/warning.png").toURI();
                    break;
                }
                case ERROR: {
                    uri = UnixNotification.class.getResource("/images/notification/error.png").toURI();
                    break;
                }
                default: {
                    uri = UnixNotification.class.getResource("/images/notification/info.png").toURI();
                    break;
                }
            }


        } catch (URISyntaxException e) {
            log.warn("Can not get image", e);
        }
        return uri;
    }

    private static void showNotification(String title, String message, URI uri) {
        try {
            Runtime runtime = Runtime.getRuntime();

            String filePath = "";
            final String[] command;

            if (uri != null) {
                filePath = new File(uri).getAbsolutePath();
                command = new String[]{
                        "/usr/bin/notify-send",
                        "-t", String.valueOf(TIME_NOTIFICATION_DELAY),
                        title, message,
                        "-i", filePath};
            } else {
                command = new String[]{
                        "/usr/bin/notify-send",
                        "-t", String.valueOf(TIME_NOTIFICATION_DELAY),
                        title, message};
            }

            runtime.exec(command);
        } catch (IOException e) {
            log.warn("Can not create notification", e);
        }
    }

    @Override
    public void showInfoNotification(String title, String message) {
        createNotification(title, message, TrayIcon.MessageType.INFO);
    }

    @Override
    public void showWarningNotification(String title, String message) {
        createNotification(title, message, TrayIcon.MessageType.WARNING);
    }

    @Override
    public void showErrorNotification(String title, String message) {
        createNotification(title, message, TrayIcon.MessageType.ERROR);
    }
}

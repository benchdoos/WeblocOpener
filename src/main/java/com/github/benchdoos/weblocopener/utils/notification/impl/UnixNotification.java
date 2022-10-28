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

import com.github.benchdoos.weblocopener.utils.notification.Notification;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;

@Log4j2
public class UnixNotification implements Notification {
    private static final int TIME_NOTIFICATION_DELAY = 50_000; //5 secs

    private static void createNotification(String title, String message, TrayIcon.MessageType messageType) {

        title = prepareString(title);
        message = prepareString(message);


        showNotification(title, message, getMessageType(messageType));
    }

    private static String prepareString(String string) {
        return string == null ? "" : string;
    }

    private static String getMessageType(TrayIcon.MessageType messageType) {
        switch (messageType) {
            case WARNING: {
                return "dialog-warning";
            }
            case ERROR: {
                return "dialog-error";
            }
            default: {
                return "dialog-information";
            }
        }
    }

    private static void showNotification(String title, String message, String type) {
        try {
            Runtime runtime = Runtime.getRuntime();

            final String[] command = new String[]{
                    "/usr/bin/notify-send",
                    "-t", String.valueOf(TIME_NOTIFICATION_DELAY),
                    title, message,
                    "--icon=" + type};

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

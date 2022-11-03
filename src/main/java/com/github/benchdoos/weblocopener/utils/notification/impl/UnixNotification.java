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

import io.jsonwebtoken.lang.Objects;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.io.IOException;

@Log4j2
public class UnixNotification extends AbstractNotification {
    private static final int TIME_NOTIFICATION_DELAY = 50_000; //5 secs

    private static String getMessageType(TrayIcon.MessageType messageType) {
        var result = "dialog-information";
        switch (messageType) {
            case WARNING -> result = "dialog-warning";
            case ERROR -> result = "dialog-error";
        }
        return result;
    }

    @Override
    public void showNotification(String title, String message, TrayIcon.MessageType messageType) {

        String preparedTitle = Objects.nullSafeToString(title);
        String preparedMessage = Objects.nullSafeToString(message);
        String stringType = getMessageType(messageType);

        showNotificationWindow(preparedTitle, preparedMessage, stringType);
    }

    private static void showNotificationWindow(String title, String message, String type) {
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
}

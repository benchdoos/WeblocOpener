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

package com.github.benchdoos.weblocopener.utils.notification;

import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.notification.NotificationManager;
import com.notification.manager.SimpleManager;
import com.notification.types.IconNotification;
import com.utils.Time;

import javax.swing.*;
import java.awt.*;

public class UnixNotification implements Notification {
    private static final int TIME_NOTIFICATION_DELAY = 5000;

    @Override
    public void showInfoNotification(String title, String message) {
        showNotification(title, message, TrayIcon.MessageType.INFO);
    }

    @Override
    public void showWarningNotification(String title, String message) {
        showNotification(title, message, TrayIcon.MessageType.WARNING);

    }

    @Override
    public void showErrorNotification(String title, String message) {
        showNotification(title, message, TrayIcon.MessageType.ERROR);
    }

    private void showNotification(String title, String message, TrayIcon.MessageType type) {
        if (PreferencesManager.isNotificationsShown()) {
            createNotification(title, message, type);
        }
    }


    private static void createNotification(String title, String message, TrayIcon.MessageType messageType) {
        IconNotification notification = new IconNotification();
        notification.setTitle(title);
        notification.setSubtitle(message);
        notification.setCloseOnClick(true);

        switch (messageType) {
            case WARNING: {
                final Image image = Toolkit.getDefaultToolkit().getImage(
                        UnixNotification.class.getResource("/images/notification/warning.png"));
                notification.setIcon(new ImageIcon(image));
                break;
            }
            case ERROR: {
                final Image image = Toolkit.getDefaultToolkit().getImage(
                        UnixNotification.class.getResource("/images/notification/error.png"));
                notification.setIcon(new ImageIcon(image));
                break;
            }
            case INFO: {
                final Image image = Toolkit.getDefaultToolkit().getImage(
                        UnixNotification.class.getResource("/images/notification/info.png"));
                notification.setIcon(new ImageIcon(image));
                break;
            }
        }
        NotificationManager manager = new SimpleManager();
        manager.addNotification(notification, Time.milliseconds(TIME_NOTIFICATION_DELAY));
    }
}

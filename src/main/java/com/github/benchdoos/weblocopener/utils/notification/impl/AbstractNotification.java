package com.github.benchdoos.weblocopener.utils.notification.impl;

import com.github.benchdoos.weblocopener.utils.notification.Notification;

import java.awt.*;

public abstract class AbstractNotification implements Notification {
    @Override
    public void showInfoNotification(String title, String message) {
        this.showNotification(title, message, TrayIcon.MessageType.INFO);
    }

    @Override
    public void showWarningNotification(String title, String message) {
        this.showNotification(title, message, TrayIcon.MessageType.WARNING);
    }

    @Override
    public void showErrorNotification(String title, String message) {
        this.showNotification(title, message, TrayIcon.MessageType.ERROR);
    }

    public abstract void showNotification(String title, String message, TrayIcon.MessageType messageType);
}

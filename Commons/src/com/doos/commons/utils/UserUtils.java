package com.doos.commons.utils;

import com.doos.commons.core.ApplicationConstants;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class UserUtils {
    private static final int MAXIMUM_MESSAGE_SIZE = 150;

    public static void showErrorMessageToUser(Component parentComponent, String title, String message) {
        FrameUtils.shakeFrame(parentComponent);
        showMessage(parentComponent, title, message, MessagePushable.ERROR_MESSAGE);
    }

    public static void showWarningMessageToUser(Component parentComponent, String title, String message) {
        FrameUtils.shakeFrame(parentComponent);
        showMessage(parentComponent, title, message, MessagePushable.WARNING_MESSAGE);
    }

    public static void showInfoMessageToUser(Component parentComponent, String title, String message) {
        Toolkit.getDefaultToolkit().beep();
        showMessage(parentComponent, title, message, MessagePushable.INFO_MESSAGE);
    }

    public static void showSuccessMessageToUser(Component parentComponent, String title, String message) {
        Toolkit.getDefaultToolkit().beep();
        showMessage(parentComponent, title, message, MessagePushable.SUCCESS_MESSAGE);
    }

    private static void showMessage(Component parentComponent, String title, String message, int messageLevel) {
        if (message.length() > MAXIMUM_MESSAGE_SIZE) {
            message = message.substring(0, Math.min(message.length(), MAXIMUM_MESSAGE_SIZE)) + "...";
        }

        Class<?> clazz = null;
        if (parentComponent != null) {
            clazz = parentComponent.getClass();
        }

        if (clazz != null && MessagePushable.class.isAssignableFrom(clazz)) {
            MessagePushable showAble = (MessagePushable) parentComponent;
            showAble.showMessage(message, messageLevel);

        } else {
            String msg;
            if (messageLevel == MessagePushable.ERROR_MESSAGE) {

                msg = "<HTML><BODY><P>" + message + " <br>Please visit " +
                        "<a href=\"" + ApplicationConstants.UPDATE_WEB_URL + "\">" + ApplicationConstants.UPDATE_WEB_URL + "</P></BODY></HTML>";
            } else {
                msg = message;
            }
            showDefaultSystemErrorMessage(parentComponent, title, msg, messageLevel);
        }
    }

    private static void showDefaultSystemErrorMessage(Component parentComponent, String title, String message, int messageLevel) {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        jEditorPane.setHighlighter(null);
        jEditorPane.setEditable(false);
        jEditorPane.getCaret().deinstall(jEditorPane);
        jEditorPane.setBackground(Color.getColor("#EEEEEE"));
        jEditorPane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                openAppGithubPage(ApplicationConstants.UPDATE_WEB_URL);
            }

        });
        jEditorPane.setText(message);
        int messageType = JOptionPane.ERROR_MESSAGE;
        if ((messageLevel == MessagePushable.INFO_MESSAGE) || (messageLevel == MessagePushable.SUCCESS_MESSAGE)) {
            messageType = JOptionPane.INFORMATION_MESSAGE;
        } else {
            if (messageLevel == MessagePushable.WARNING_MESSAGE) {
                messageType = JOptionPane.WARNING_MESSAGE;
            }
        }


        JOptionPane.showMessageDialog(parentComponent,
                jEditorPane,
                "[WeblocOpener] " + title, messageType);
    }

    public static void openAppGithubPage(String url) {
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();

        try {
            desktop.browse(URI.create(url));
        } catch (IOException e) {
            final String message = "URL is corrupt: ";
            showErrorMessageToUser(null, message, message + url);
        }

    }
}

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

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopenercore.constants.ApplicationConstants;
import com.github.benchdoos.weblocopenercore.constants.StringConstants;
import com.github.benchdoos.weblocopenercore.service.UrlsProceed;
import com.github.benchdoos.weblocopenercore.utils.FrameUtils;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;

public class ForcedNotification extends AbstractNotification {
    private static final int MAXIMUM_MESSAGE_SIZE = 150;

    private final Component component;

    public ForcedNotification(Component component) {
        this.component = component;
    }

    private void showDefaultSystemErrorMessage(Component parentComponent, String title, String message,
                                               TrayIcon.MessageType messageType) {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        jEditorPane.setHighlighter(null);
        jEditorPane.setEditable(false);
        jEditorPane.getCaret().deinstall(jEditorPane);
        jEditorPane.setBackground(Color.getColor("#EEEEEE"));
        jEditorPane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                UrlsProceed.openUrl(StringConstants.UPDATE_WEB_URL);
            }

        });
        jEditorPane.setText(message);

        int optionPaneType = JOptionPane.ERROR_MESSAGE;
        switch (messageType) {
            case NONE, INFO -> optionPaneType = JOptionPane.INFORMATION_MESSAGE;
            case WARNING -> optionPaneType = JOptionPane.WARNING_MESSAGE;
        }

        if (title == null) {
            title = "";
        }

        JOptionPane.showMessageDialog(parentComponent,
                jEditorPane,
                "[" + ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME + "] " + title, optionPaneType);
    }

    @Override
    public void showNotification(String title, String message, TrayIcon.MessageType messageType) {
        if (message.length() > MAXIMUM_MESSAGE_SIZE) {
            message = message.substring(0, MAXIMUM_MESSAGE_SIZE) + "...";
        }

        String msg;
        if (messageType == TrayIcon.MessageType.ERROR) {

            msg = "<HTML><BODY><P>" + message + "<br>" + Translation.getTranslatedString("CommonsBundle", "pleaseVisitMessage") + " " +
                    "<a href=\"" + StringConstants.UPDATE_WEB_URL + "\">" + StringConstants.UPDATE_WEB_URL + "</P></BODY></HTML>";
        } else {
            msg = message;
        }
        showDefaultSystemErrorMessage(component, title, msg, messageType);
    }

    @Override
    public void showWarningNotification(String title, String message) {
        FrameUtils.shakeFrame(component);
        showNotification(title, message, TrayIcon.MessageType.WARNING);
    }

    @Override
    public void showErrorNotification(String title, String message) {
        FrameUtils.shakeFrame(component);
        showNotification(title, message, TrayIcon.MessageType.ERROR);
    }

}

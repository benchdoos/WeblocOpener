package com.doos.commons.utils;

import com.doos.commons.ApplicationConstants;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class UserUtils {
    public static void showErrorMessageToUser(Component parentComponent, String title, String message) {
        String msg = "<HTML><BODY><P>" + message + " <br>Please visit " +
                "<a href=\"" + ApplicationConstants.UPDATE_WEB_URL + "\">" + ApplicationConstants.UPDATE_WEB_URL + "</P></BODY></HTML>";
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        jEditorPane.setHighlighter(null);
        jEditorPane.setEditable(false);
        jEditorPane.getCaret().deinstall(jEditorPane);
        jEditorPane.setBackground(Color.getColor("#EEEEEE"));
        jEditorPane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                openAppGithubPage();
            }

        });
        jEditorPane.setText(msg);
        JOptionPane.showMessageDialog(parentComponent,
                                      jEditorPane,
                                      "[WeblocOpener] " + title, JOptionPane.ERROR_MESSAGE);
    }

    public static void openAppGithubPage() {
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();

        try {
            desktop.browse(URI.create(ApplicationConstants.UPDATE_WEB_URL));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new Frame(), "URL is corrupt: " + ApplicationConstants.UPDATE_WEB_URL);
        }

    }
}

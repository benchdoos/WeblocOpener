package com.doos.settings_manager.core;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryCanNotWriteInfoException;
import com.doos.settings_manager.registry.RegistryManager;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Eugene Zrazhevsky on 20.11.2016.
 *
 * {@code SettingsManager} manages settings of application. It uses registry of Windows.
 */
public class SettingsManager {

    //You can not call this, method can be deleted
    public static void loadInfo() throws RegistryCanNotReadInfoException, RegistryCanNotWriteInfoException {
        RegistryManager.getAppNameValue();
        RegistryManager.getAppVersionValue();
        RegistryManager.getInstallLocationValue();
        RegistryManager.getURLUpdateValue();

        //Fixes registry after update (if needed)
        if (!RegistryManager.getAppVersionValue().equals(ApplicationConstants.APP_VERSION)) {
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        }
    }

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

    private static void openAppGithubPage() {
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

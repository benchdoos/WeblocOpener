package com.doos.settings_manager.core;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryCanNotWriteInfoException;
import com.doos.settings_manager.registry.RegistryManager;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

/**
 * Created by Eugene Zrazhevsky on 20.11.2016.
 */
public class SettingsManager {

    public static void updateInfo(Properties properties) {
        try {
            final String property = properties.getProperty(RegistryManager.KEY_AUTO_UPDATE);
            if (property != null) {
                RegistryManager.setAutoUpdateActive(Boolean.parseBoolean(property));
            } else {
                RegistryManager.setAutoUpdateActive(true);
            }
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        } catch (RegistryCanNotWriteInfoException e) {
            e.printStackTrace();
        }
    }

    public static Properties loadInfo() throws RegistryCanNotReadInfoException, RegistryCanNotWriteInfoException {
        Properties result = new Properties();
        result.setProperty(RegistryManager.KEY_AUTO_UPDATE, Boolean.toString(RegistryManager.isAutoUpdateActive()));

        result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());

        //Fixes com.doos.com.doos.settings_manager.core.settings_manager.registry after update (if needed)
        if (!RegistryManager.getAppVersionValue().equals(ApplicationConstants.APP_VERSION)) {
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        }
        result.setProperty(RegistryManager.KEY_INSTALL_LOCATION, RegistryManager.getInstallLocationValue());
        return result;
    }

    public static void showErrorMessage(String title, String message) {
        String msg = "<HTML><BODY><P>" + message + " <br>Please visit " +
                "<a href=\"" + ApplicationConstants.UPDATE_WEB_URL + "\">" + ApplicationConstants.UPDATE_WEB_URL + "</P></BODY></HTML>";
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        jEditorPane.setHighlighter(null);
        jEditorPane.setEditable(false);
        jEditorPane.getCaret().deinstall(jEditorPane);
        jEditorPane.setBackground(Color.getColor("#EEEEEE"));
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    openUrl(ApplicationConstants.UPDATE_WEB_URL);
                }

            }
        });
        jEditorPane.setText(msg);
        JOptionPane.showMessageDialog(null,
                                      jEditorPane,
                                      "[WeblocOpener] " + title, JOptionPane.ERROR_MESSAGE);
    }

    private static void openUrl(String url) {
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();

        try {
            desktop.browse(URI.create(url));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new Frame(), "URL is corrupt: " + url);
        }

    }
}

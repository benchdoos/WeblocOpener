package com.doos.settings_manager.core;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryCanNotWriteInfoException;
import com.doos.settings_manager.registry.RegistryException;
import com.doos.settings_manager.registry.RegistryManager;
import com.doos.settings_manager.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.settings_manager.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

/**
 * Created by Eugene Zrazhevsky on 20.11.2016.
 */
public class SettingsManager {
    private static final String DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH =
            "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + ApplicationConstants.APP_ID;

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

    public static Properties fixRegistry()
            throws RegistryException, FileNotFoundException {
        System.out.println(
                "[REGISTRY FIXER] Trying to check and fix com.doos.com.doos.settings_manager.core.settings_manager.registry values.");
        Properties result = new Properties();
        try {
            result.setProperty(RegistryManager.KEY_AUTO_UPDATE, Boolean.toString(RegistryManager.isAutoUpdateActive()));

        } catch (RegistryCanNotReadInfoException e) {
            try {

                RegistryManager.setAutoUpdateActive(ApplicationConstants.APP_AUTO_UPDATE_DEFAULT_VALUE);
                System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_AUTO_UPDATE);
                result.setProperty(RegistryManager.KEY_AUTO_UPDATE,
                                   Boolean.toString(RegistryManager.isAutoUpdateActive()));
            } catch (RegistryCanNotWriteInfoException e1) {
                throw new RegistryFixerAutoUpdateKeyFailException("", e);
            }
        } finally {
            try {
                result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());
            } catch (RegistryCanNotReadInfoException e) {
                try {
                    RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
                    result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());
                    System.out.println("[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_CURRENT_VERSION);
                } catch (RegistryCanNotWriteInfoException e1) {
                    throw new RegistryFixerAppVersionKeyFailException("", e);
                }
            } finally {
                try {
                    result.setProperty(RegistryManager.KEY_INSTALL_LOCATION, RegistryManager.getInstallLocationValue());
                } catch (RegistryCanNotReadInfoException e) {
                    String path;
                    try {
                        path = WinReg.HKEY_LOCAL_MACHINE + "\\" +
                                DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH + "\\" +
                                RegistryManager.KEY_INSTALL_LOCATION;
                        System.out.println("PPPPPP:" + path);
                        path = Advapi32Util
                                .registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
                                                        DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH,
                                                        RegistryManager.KEY_INSTALL_LOCATION);

                    } catch (Exception e1) {
                        throw new RegistryCanNotReadInfoException(
                                "Can not read value from Windows UnInstaller: " + "HKLM\\" +
                                        DEFAULT_INSTALLER_UNINSTALL_LOCATION_PATH, e);
                    }

                    if (!path.isEmpty()) {
                        if (new File(path).exists() && new File(path).isDirectory()) {
                            RegistryManager.setInstallLocationValue(path);
                            result.setProperty(RegistryManager.KEY_INSTALL_LOCATION,
                                               RegistryManager.getInstallLocationValue());
                            System.out.println(
                                    "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);
                        } else {
                            throw new FileNotFoundException(
                                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                                            "FOLDER]: " + path);
                        }
                    } else {
                        if (new File(path).exists() && new File(path).isDirectory() && new File(path).getName().equals
                                (ApplicationConstants.APP_NAME)) {
                            RegistryManager.setInstallLocationValue(path);
                            result.setProperty(RegistryManager.KEY_INSTALL_LOCATION,
                                               RegistryManager.getInstallLocationValue());
                            System.out.println(
                                    "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_INSTALL_LOCATION);

                        } else {
                            throw new FileNotFoundException(
                                    "It is not a directory or does not exist [WINDOWS UNINSTALL " +
                                            "FOLDER]: [" + path + "]");
                        }
                    }
                } finally {
                    try {
                        RegistryManager.getAppNameValue();
                    } catch (RegistryCanNotReadInfoException e1) {
                        try {
                            RegistryManager.setAppNameValue(ApplicationConstants.APP_NAME);
                            System.out.println(
                                    "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_APP_NAME);
                        } catch (RegistryCanNotWriteInfoException ignore) {/*NOP*/
                        } finally {
                            try {
                                RegistryManager.getURLUpdateValue();
                            } catch (RegistryCanNotReadInfoException e) {
                                try {
                                    RegistryManager.setURLUpdateValue(ApplicationConstants.UPDATE_WEB_URL);
                                    System.out.println(
                                            "[REGISTRY FIXER] Successfully Fixed " + RegistryManager.KEY_URL_UPDATE_LINK);
                                } catch (RegistryCanNotWriteInfoException e2) {/*NOP*/
                                } finally {/*Add here sub-directories if needed*/}
                            }
                        }

                    }
                }
            }
        }

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

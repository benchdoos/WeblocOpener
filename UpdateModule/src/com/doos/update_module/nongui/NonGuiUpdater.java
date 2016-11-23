package com.doos.update_module.nongui;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.registry.RegistryManager;
import com.doos.update_module.core.Main;
import com.doos.update_module.gui.UpdateDialog;
import com.doos.update_module.update.AppVersion;
import com.doos.update_module.update.Updater;
import com.doos.update_module.utils.Internal;

import java.awt.*;
import java.awt.event.*;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {
    public static final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
            .getImage(NonGuiUpdater.class.getResource("/icon.png")));
    public static final SystemTray tray = SystemTray.getSystemTray();
    public static AppVersion serverAppVersion = null;


    public NonGuiUpdater() {
        Updater updater = new Updater();
        serverAppVersion = updater.getAppVersion();
        compareVersions(serverAppVersion);
    }

    private void compareVersions(AppVersion appVersion) {
        String str = Main.properties.getProperty(RegistryManager.KEY_CURRENT_VERSION);
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) < 0) {
            //create trayicon and show pop-up
            createTrayIcon();
            trayIcon.displayMessage(ApplicationConstants.APP_NAME + " - Updater",
                                    "There is a new version of application:" + serverAppVersion.getVersion(),
                                    TrayIcon.MessageType.INFO);
        } else if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
            //System.exit(0);

        }
    }

    private void createTrayIcon() {

        PopupMenu trayMenu = new PopupMenu();

        final CheckboxMenuItem autoUpdateCheckBox = new CheckboxMenuItem("Auto-update");
        System.out.println(
                RegistryManager.KEY_AUTO_UPDATE + ": " + Main.properties.getProperty(RegistryManager.KEY_AUTO_UPDATE));
        autoUpdateCheckBox.setState(Boolean.parseBoolean(Main.properties.getProperty(RegistryManager.KEY_AUTO_UPDATE)));
        autoUpdateCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(RegistryManager.KEY_AUTO_UPDATE + ": " + autoUpdateCheckBox.getState());
                Main.properties.setProperty(RegistryManager.KEY_AUTO_UPDATE, autoUpdateCheckBox.getState() + "");
                Main.updateProperties();
            }
        });
        trayMenu.add(autoUpdateCheckBox);
        trayMenu.addSeparator();

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0); //FIXME
            }
        });
        trayMenu.add(exit);

        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(trayMenu);

        trayIcon.addMouseListener(new MouseAdapter() {
            UpdateDialog updateDialog = null;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    trayIcon.removeMouseListener(this);
                    Main.initUpdateJar();
                    tray.remove(trayIcon);
                    /*if (updateDialog != null) {
                        updateDialog.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                try {
                                    Main.loadProperties();
                                } catch (RegistryException e1) {
                                    e1.printStackTrace();
                                }
                                String str = properties.getProperty(RegistryManager.KEY_CURRENT_VERSION);

                                if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
                                    tray.remove(trayIcon);
                                    System.exit(0);
                                }
                                super.windowClosing(e);

                            }

                        });
                    }*/

                    /*updateDialog.setVisible(true);
                    updateDialog.checkForUpdates();*/


                }
                super.mouseClicked(e);
            }
        });
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}


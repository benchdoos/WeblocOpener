package com.doos.nongui;

import com.doos.core.Main;
import com.doos.gui.UpdateDialog;
import com.doos.update.AppVersion;
import com.doos.update.Updater;
import com.doos.utils.ApplicationConstants;
import com.doos.utils.Internal;

import java.awt.*;
import java.awt.event.*;

import static com.doos.core.Main.*;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {
    private static AppVersion serverAppVersion = null;
    TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/icon.png")));


    public NonGuiUpdater() {
        Updater updater = new Updater();
        serverAppVersion = updater.getAppVersion();
        compareVersions(serverAppVersion);
        System.out.println("hello");
    }

    private void compareVersions(AppVersion appVersion) {
        String str = properties.getProperty(CURRENT_APP_VERSION);
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) < 0) {
            //create trayicon and show pop-up
            createTrayIcon();
            trayIcon.displayMessage(ApplicationConstants.APP_NAME + " - Updater",
                    "There is a new version of application:" + serverAppVersion.getVersion(),
                    TrayIcon.MessageType.INFO);
        } else if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
            System.exit(0);

        }
    }

    private void createTrayIcon() {
        SystemTray tray = SystemTray.getSystemTray();

        PopupMenu trayMenu = new PopupMenu();

        CheckboxMenuItem autoUpdateCheckBox = new CheckboxMenuItem("Auto-update");
        System.out.println(">>" + properties.getProperty(UPDATE_ACTIVE));
        autoUpdateCheckBox.setState(properties.getProperty(UPDATE_ACTIVE).equals("true"));
        autoUpdateCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(">>>" + autoUpdateCheckBox.getState());
                properties.setProperty(UPDATE_ACTIVE, autoUpdateCheckBox.getState() + "");
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
                System.exit(0);
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
                    //trayIcon.removeMouseListener(this);
                    if (updateDialog == null) {
                        updateDialog = new UpdateDialog();
                    }
                    updateDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            Main.loadProperties();
                            String str = properties.getProperty(CURRENT_APP_VERSION);

                            if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
                                tray.remove(trayIcon);
                                System.exit(0);
                            }
                            super.windowClosing(e);

                        }
                    });

                    updateDialog.setVisible(true);
                    updateDialog.checkForUpdates();


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


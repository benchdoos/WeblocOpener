package com.doos.update_module.nongui;

import com.doos.commons.ApplicationConstants;
import com.doos.commons.registry.RegistryCanNotReadInfoException;
import com.doos.commons.registry.RegistryCanNotWriteInfoException;
import com.doos.commons.registry.RegistryManager;
import com.doos.commons.utils.Internal;
import com.doos.update_module.core.Main;
import com.doos.update_module.update.AppVersion;
import com.doos.update_module.update.Updater;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {
    public static final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
                                                                 .getImage(
                                                                         NonGuiUpdater.class.getResource("/icon.png")));
    public static final SystemTray tray = SystemTray.getSystemTray();
    private AppVersion serverAppVersion = null;


    public NonGuiUpdater() {
        Updater updater = new Updater();
        serverAppVersion = updater.getAppVersion();
        compareVersions();
    }

    private void compareVersions() {
        String str = ApplicationConstants.APP_VERSION;
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) < 0) {
            //create trayicon and show pop-up
            createTrayIcon();
            trayIcon.displayMessage(ApplicationConstants.APP_NAME + " - Updater",
                                    "There is a new version of application:" + serverAppVersion.getVersion(),
                                    TrayIcon.MessageType.INFO);
        } /*else if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
            //System.exit(0);

        }*/
    }

    private void createTrayIcon() {

        PopupMenu trayMenu = new PopupMenu();

        final CheckboxMenuItem autoUpdateCheckBox = new CheckboxMenuItem("Auto-update");
        try {
            System.out.println(
                    RegistryManager.KEY_AUTO_UPDATE + ": " + RegistryManager.isAutoUpdateActive());
        } catch (RegistryCanNotReadInfoException ignore) {/*NOP*/}

        try {
            autoUpdateCheckBox.setState(RegistryManager.isAutoUpdateActive());
        } catch (RegistryCanNotReadInfoException e) {
            RegistryManager.setDefaultSettings();
            autoUpdateCheckBox.setState(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
        }
        autoUpdateCheckBox.addItemListener(e -> {
            System.out.println(RegistryManager.KEY_AUTO_UPDATE + ": " + autoUpdateCheckBox.getState());
            try {
                RegistryManager.setAutoUpdateActive(autoUpdateCheckBox.getState());
            } catch (RegistryCanNotWriteInfoException e1) {
                RegistryManager.setDefaultSettings();
            }
        });
        trayMenu.add(autoUpdateCheckBox);
        trayMenu.addSeparator();

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> {
            tray.remove(trayIcon);
            //System.exit(0); //FIXME
        });
        trayMenu.add(exit);

        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(trayMenu);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    trayIcon.removeMouseListener(this);
                    Main.initUpdateJar();
                    tray.remove(trayIcon);
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


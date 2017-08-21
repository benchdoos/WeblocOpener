package com.doos.update_module.nongui;

import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.core.Translation;
import com.doos.commons.registry.RegistryCanNotReadInfoException;
import com.doos.commons.registry.RegistryCanNotWriteInfoException;
import com.doos.commons.registry.RegistryManager;
import com.doos.commons.utils.Internal;
import com.doos.update_module.core.Main;
import com.doos.update_module.update.AppVersion;
import com.doos.update_module.update.Updater;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static com.doos.commons.utils.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {
    public static final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
            NonGuiUpdater.class.getResource("/icon.png")));
    public static final SystemTray tray = SystemTray.getSystemTray();
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    private AppVersion serverAppVersion = null;


    public NonGuiUpdater() {
        Updater updater;
        try {
            updater = new Updater();

            serverAppVersion = updater.getAppVersion();
            compareVersions();
        } catch (IOException e) {
            Updater.canNotConnectManage(e);
        }
    }

    private void compareVersions() {
        String str = ApplicationConstants.APP_VERSION;
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) < 0) {
            //create trayicon and show pop-up
            createTrayIcon();


            final String[] displayMessage = new String[1];
            Translation translation = new Translation("translations/UpdateDialogBundle") {
                @Override
                public void initTranslations() {
                    displayMessage[0] = messages.getString("newVersionAvailableTrayNotification");
                }
            };
            translation.initTranslations();
            trayIcon.displayMessage(ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME + " - Updater",
                    displayMessage[0] + ": " + serverAppVersion.getVersion(),
                    TrayIcon.MessageType.INFO);
        }
    }

    private void createTrayIcon() {

        PopupMenu trayMenu = new PopupMenu();

        final CheckboxMenuItem autoUpdateCheckBox = new CheckboxMenuItem("Auto-update");
        try {
            log.debug(RegistryManager.KEY_AUTO_UPDATE + ": " + RegistryManager.isAutoUpdateActive());
        } catch (RegistryCanNotReadInfoException ignore) {/*NOP*/}

        try {
            autoUpdateCheckBox.setState(RegistryManager.isAutoUpdateActive());
        } catch (RegistryCanNotReadInfoException e) {
            RegistryManager.setDefaultSettings();
            autoUpdateCheckBox.setState(ApplicationConstants.IS_APP_AUTO_UPDATE_DEFAULT_VALUE);
        }
        autoUpdateCheckBox.addItemListener(e -> {
            log.debug(RegistryManager.KEY_AUTO_UPDATE + ": " + autoUpdateCheckBox.getState());
            try {
                RegistryManager.setAutoUpdateActive(autoUpdateCheckBox.getState());
            } catch (RegistryCanNotWriteInfoException e1) {
                RegistryManager.setDefaultSettings();
            }
        });
        trayMenu.add(autoUpdateCheckBox);
        trayMenu.addSeparator();

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> tray.remove(trayIcon));
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


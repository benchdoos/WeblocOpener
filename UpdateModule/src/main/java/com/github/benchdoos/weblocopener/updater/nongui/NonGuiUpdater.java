/*
 * Copyright 2018 Eugeny Zrazhevsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.benchdoos.weblocopener.updater.nongui;

import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.core.Translation;
import com.github.benchdoos.weblocopener.commons.registry.RegistryManager;
import com.github.benchdoos.weblocopener.commons.utils.Internal;
import com.github.benchdoos.weblocopener.commons.utils.Logging;
import com.github.benchdoos.weblocopener.updater.core.Main;
import com.github.benchdoos.weblocopener.updater.update.AppVersion;
import com.github.benchdoos.weblocopener.updater.update.Updater;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {
    public static final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
            NonGuiUpdater.class.getResource("/updaterIcon16.png")));
    public static final SystemTray tray = SystemTray.getSystemTray();
    private static final Logger log = Logger.getLogger(Logging.getCurrentClassName());
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
        log.debug(RegistryManager.KEY_AUTO_UPDATE + ": " + RegistryManager.isAutoUpdateActive());
        autoUpdateCheckBox.setState(RegistryManager.isAutoUpdateActive());
        autoUpdateCheckBox.addItemListener(e -> {
            log.debug(RegistryManager.KEY_AUTO_UPDATE + ": " + autoUpdateCheckBox.getState());
            RegistryManager.setAutoUpdateActive(autoUpdateCheckBox.getState());
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

        trayIcon.setToolTip("WeblocOpener Updater");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}


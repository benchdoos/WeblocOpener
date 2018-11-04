/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
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

package com.github.benchdoos.weblocopener.nongui;

import com.github.benchdoos.weblocopener.core.Application;
import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.registry.RegistryManager;
import com.github.benchdoos.weblocopener.update.AppVersion;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.Internal;
import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static com.github.benchdoos.weblocopener.utils.system.SystemUtils.IS_WINDOWS_XP;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {
    public static final SystemTray tray = SystemTray.getSystemTray();
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    public static TrayIcon trayIcon;
    private String toolTipText = "WeblocOpener - Update";
    private Translation translation;


    private AppVersion serverAppVersion = null;


    public NonGuiUpdater() {

        initGui();

        Updater updater;
        try {
            updater = new Updater();

            serverAppVersion = updater.getAppVersion();
            translateDialog();
            compareVersions();
        } catch (IOException e) {
            Updater.canNotConnectManage(e);
        }
    }

    private void compareVersions() {
        String str = RegistryManager.isDevMode() ? "1.0.0.0" : CoreUtils.getApplicationVersionString();
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
            trayIcon.displayMessage(toolTipText, displayMessage[0] + ": " + serverAppVersion.getVersion(),
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
                    Application.initUpdateJar();
                    tray.remove(trayIcon);
                }
                super.mouseClicked(e);
            }
        });

        trayIcon.setToolTip(toolTipText);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void initGui() {
        if (IS_WINDOWS_XP) {
            trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
                    NonGuiUpdater.class.getResource("/images/updateIconWhite256.png")));
        } else {
            trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
                    NonGuiUpdater.class.getResource("/images/updateIconBlue256.png")));
        }
    }

    private void translateDialog() {
        translation = new Translation("translations/UpdateDialogBundle") {
            @Override
            public void initTranslations() {
                toolTipText = messages.getString("windowTitle");
            }
        };
        translation.initTranslations();
    }

}


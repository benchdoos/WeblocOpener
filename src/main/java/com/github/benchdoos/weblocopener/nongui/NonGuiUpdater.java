/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
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
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.update.AppVersion;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.update.UpdaterManager;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.Internal;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.github.benchdoos.weblocopener.utils.system.SystemUtils.IS_WINDOWS_XP;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {
    public static final SystemTray tray = SystemTray.getSystemTray();
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    public static TrayIcon trayIcon;


    private AppVersion serverAppVersion = null;


    public NonGuiUpdater() {

        initGui();

        Updater updater;
        updater = UpdaterManager.getUpdaterForCurrentOperatingSystem();

        final AppVersion latestAppVersion = updater.getLatestAppVersion();
        if (latestAppVersion != null) {
            serverAppVersion = latestAppVersion;
            compareVersions();
        } else {
            log.warn("Can not get server version");
            if (Application.updateMode != Application.UPDATE_MODE.SILENT) {
                Translation translation = new Translation("UpdaterBundle");
                UserUtils.showErrorMessageToUser(null, translation.getTranslatedString("canNotUpdateTitle"),
                        translation.getTranslatedString("canNotUpdateMessage"));
            }

        }
    }

    private void compareVersions() {
        String str = PreferencesManager.isDevMode() ? "1.0.0.0" : CoreUtils.getApplicationVersionString();
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) < 0) {
            //create tray icon and show pop-up
            createTrayIcon();

            trayIcon.displayMessage(Translation.getTranslatedString("UpdateDialogBundle", "windowTitle"),
                    Translation.getTranslatedString("UpdateDialogBundle",
                            "newVersionAvailableTrayNotification")
                            + ": " + serverAppVersion.getVersion(),
                    TrayIcon.MessageType.INFO);
        }
    }

    private CheckboxMenuItem createCheckBoxItem() {
        final CheckboxMenuItem autoUpdateCheckBox = new CheckboxMenuItem(
                Translation.getTranslatedString("NonGuiUpdaterBundle", "autoUpdateCheckBox"));

        log.debug(PreferencesManager.KEY_AUTO_UPDATE + ": " + PreferencesManager.isAutoUpdateActive());
        autoUpdateCheckBox.setState(PreferencesManager.isAutoUpdateActive());
        autoUpdateCheckBox.addItemListener(e -> {
            log.debug(PreferencesManager.KEY_AUTO_UPDATE + ": " + autoUpdateCheckBox.getState());
            PreferencesManager.setAutoUpdateActive(autoUpdateCheckBox.getState());
        });
        return autoUpdateCheckBox;
    }

    private MenuItem createExitItem() {
        MenuItem exit = new MenuItem(
                Translation.getTranslatedString("NonGuiUpdaterBundle", "exitButton"));

        exit.addActionListener(e -> tray.remove(trayIcon));
        return exit;
    }

    private MenuItem createSettingsItem() {
        MenuItem settings = new MenuItem(
                Translation.getTranslatedString("NonGuiUpdaterBundle", "settingsButton"));
        settings.addActionListener(e -> {
            Application.runSettingsDialog();
            tray.remove(trayIcon);
        });
        return settings;
    }

    private void createTrayIcon() {

        PopupMenu trayMenu = new PopupMenu();

        MenuItem settings = createSettingsItem();
        trayMenu.add(settings);
        trayMenu.addSeparator();


        final CheckboxMenuItem autoUpdateCheckBox = createCheckBoxItem();
        trayMenu.add(autoUpdateCheckBox);
        trayMenu.addSeparator();

        MenuItem exit = createExitItem();
        trayMenu.add(exit);

        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(trayMenu);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    trayIcon.removeMouseListener(this);
                    Application.runUpdateDialog();
                    tray.remove(trayIcon);
                }
                super.mouseClicked(e);
            }
        });

        trayIcon.setToolTip(Translation.getTranslatedString("UpdateDialogBundle", "windowTitle"));
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
}


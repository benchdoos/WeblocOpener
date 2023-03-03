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

package com.github.benchdoos.weblocopener.nongui.notify.impl;

import com.github.benchdoos.weblocopener.core.Application;
import com.github.benchdoos.weblocopener.nongui.NonGuiUpdater;
import com.github.benchdoos.weblocopener.nongui.notify.Notifier;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.service.settings.impl.AutoUpdateSettings;
import com.github.benchdoos.weblocopenercore.service.translation.Translation;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WindowsNotifier implements Notifier {
  private static final SystemTray tray = SystemTray.getSystemTray();
  private TrayIcon trayIcon;

  public WindowsNotifier() {
    initGui();
  }

  public void notifyUser(AppVersion serverApplicationVersion) {
    createTrayIcon();
    showUpdateAvailableTrayIconMessage(serverApplicationVersion);
  }

  private void createTrayIcon() {

    PopupMenu trayMenu = new PopupMenu();

    MenuItem update = createUpdateItem();
    trayMenu.add(update);
    trayMenu.addSeparator();

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

    trayIcon.addMouseListener(
        new MouseAdapter() {
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

    trayIcon.setToolTip(Translation.get("UpdateDialogBundle", "windowTitle"));
    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      log.warn("Can not add trayIcon", e);
    }
  }

  private void initGui() {
    trayIcon =
        new TrayIcon(
            Toolkit.getDefaultToolkit()
                .getImage(NonGuiUpdater.class.getResource("/images/updateIconBlue256.png")));
  }

  private MenuItem createUpdateItem() {
    MenuItem update = new MenuItem(Translation.get("NonGuiUpdaterBundle", "openUpdaterButton"));
    update.addActionListener(
        e -> {
          com.github.benchdoos.weblocopener.core.Application.runUpdateDialog();
          tray.remove(trayIcon);
        });
    return update;
  }

  private MenuItem createSettingsItem() {
    MenuItem settings = new MenuItem(Translation.get("NonGuiUpdaterBundle", "settingsButton"));
    settings.addActionListener(
        e -> {
          com.github.benchdoos.weblocopenercore.core.Application.runSettingsDialog(
              Application.getApplicationPath());
          tray.remove(trayIcon);
        });
    return settings;
  }

  private MenuItem createExitItem() {
    MenuItem exit = new MenuItem(Translation.get("NonGuiUpdaterBundle", "exitButton"));

    exit.addActionListener(e -> tray.remove(trayIcon));
    return exit;
  }

  private void showUpdateAvailableTrayIconMessage(AppVersion serverApplicationVersion) {
    Translation translation = new Translation("UpdateDialogBundle");
    final String windowTitle = translation.get("windowTitle");
    final String windowMessage =
        translation.get("newVersionAvailableTrayNotification")
            + ": "
            + serverApplicationVersion.version();
    trayIcon.displayMessage(windowTitle, windowMessage, TrayIcon.MessageType.INFO);
  }

  private CheckboxMenuItem createCheckBoxItem() {
    final CheckboxMenuItem autoUpdateCheckBox =
        new CheckboxMenuItem(Translation.get("NonGuiUpdaterBundle", "autoUpdateCheckBox"));

    Boolean isAutoUpdateEnabled = new AutoUpdateSettings().getValue();
    log.debug("Auto update enabled: {}", isAutoUpdateEnabled);
    autoUpdateCheckBox.setState(isAutoUpdateEnabled);
    autoUpdateCheckBox.addItemListener(
        e -> {
          log.debug("Auto update checkbox selected: " + autoUpdateCheckBox.getState());
          new AutoUpdateSettings().save(autoUpdateCheckBox.getState());
        });
    return autoUpdateCheckBox;
  }
}

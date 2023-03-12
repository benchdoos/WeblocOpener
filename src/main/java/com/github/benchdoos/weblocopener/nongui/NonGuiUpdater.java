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
import com.github.benchdoos.weblocopener.nongui.notify.NotifyManager;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.utils.UpdateHelperUtil;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.exceptions.NoAvailableVersionException;
import com.github.benchdoos.weblocopenercore.service.notification.NotificationManager;
import com.github.benchdoos.weblocopenercore.service.settings.impl.DevModeSettings;
import com.github.benchdoos.weblocopenercore.service.translation.Translation;
import com.github.benchdoos.weblocopenercore.utils.CoreUtils;
import com.github.benchdoos.weblocopenercore.utils.VersionUtils;
import lombok.extern.log4j.Log4j2;

/** Created by Eugene Zrazhevsky on 04.11.2016. */
@Log4j2
public class NonGuiUpdater {
  private final Updater updater;
  private AppVersion serverApplicationVersion;

  public NonGuiUpdater() {

    updater = UpdateHelperUtil.getUpdaterForCurrentOS();

    final AppVersion latestAppVersion = updater.getLatestAppVersion();
    if (latestAppVersion != null) {
      serverApplicationVersion = latestAppVersion;
      compareVersions();
    } else {
      log.warn("Can not get server version");
      if (Application.updateMode != Application.UPDATE_MODE.SILENT) {
        Translation translation = new Translation("UpdaterBundle");
        NotificationManager.getForcedNotification(null)
            .showErrorNotification(
                translation.get("canNotUpdateTitle"), translation.get("canNotUpdateMessage"));
      }
    }
  }

  private void compareVersions() {

    if (Boolean.FALSE.equals(new DevModeSettings().getValue())) {
      switch (VersionUtils.versionCompare(
              serverApplicationVersion, CoreUtils.getCurrentAppVersion())
          .getVersionCompare()) {
        case FIRST_VERSION_IS_NEWER -> onNewVersionAvailable();
        case SECOND_VERSION_IS_NEWER -> log.info(
            "Current version is newer! Current: {}, Server: {}",
            CoreUtils.getCurrentAppVersion(),
            serverApplicationVersion);
        case VERSIONS_ARE_EQUAL -> log.info(
            "There are no updates available. Versions are equal! Current: {}, Server: {}",
            CoreUtils.getCurrentAppVersion(),
            serverApplicationVersion);
      }
    } else onNewVersionAvailable();
  }

  private void onNewVersionAvailable() {

    try {
      final AppVersion.Asset asset = updater.getInstallerAsset(serverApplicationVersion);
      if (asset != null) {
        log.info(
            "Showing notification: New version is available! Current: {}, Server: {}",
            CoreUtils.getCurrentAppVersion(),
            serverApplicationVersion);

        NotifyManager.getNotifierForSystem().notifyUser(serverApplicationVersion);
      } else {
        log.warn(
            "Update is available, but there is no version for current system: {}",
            serverApplicationVersion);
      }
    } catch (NoAvailableVersionException e) {
      log.warn("No asset available for version: {}", serverApplicationVersion, e);
    }
  }
}

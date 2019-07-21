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
import com.github.benchdoos.weblocopener.nongui.notify.NotifyManager;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.update.UpdaterManager;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.Internal;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.notification.NotificationManager;
import com.github.benchdoos.weblocopener.utils.version.ApplicationVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Eugene Zrazhevsky on 04.11.2016.
 */


public class NonGuiUpdater {

    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());


    private ApplicationVersion serverApplicationVersion = null;


    public NonGuiUpdater() {

        Updater updater;
        updater = UpdaterManager.getUpdaterForCurrentOperatingSystem();

        final ApplicationVersion latestApplicationVersion = updater.getLatestAppVersion();
        if (latestApplicationVersion != null) {
            serverApplicationVersion = latestApplicationVersion;
            compareVersions();
        } else {
            log.warn("Can not get server version");
            if (Application.updateMode != Application.UPDATE_MODE.SILENT) {
                Translation translation = new Translation("UpdaterBundle");
                NotificationManager.getForcedNotification(null).showErrorNotification(
                        translation.getTranslatedString("canNotUpdateTitle"),
                        translation.getTranslatedString("canNotUpdateMessage"));
            }

        }
    }

    private void compareVersions() {

        if (!PreferencesManager.isDevMode()) {
            switch (Internal.versionCompare(serverApplicationVersion)) {
                case SERVER_VERSION_IS_NEWER:
                    onNewVersionAvailable();
                    break;
                case CURRENT_VERSION_IS_NEWER:
                    log.info("Current version is newer! Current: {}, Server: {}",
                            CoreUtils.getCurrentApplicationVersion(), serverApplicationVersion);
                    break;
                case VERSIONS_ARE_EQUAL:
                    log.info("There are no updates available. Versions are equal! Current: {}, Server: {}",
                            CoreUtils.getCurrentApplicationVersion(), serverApplicationVersion);
                    break;
            }
        } else onNewVersionAvailable();
    }

    private void onNewVersionAvailable() {
        if (serverApplicationVersion.getDownloadUrl() != null) {
            log.info("Showing notification: New version is available! Current: {}, Server: {}",
                    CoreUtils.getCurrentApplicationVersion(), serverApplicationVersion);

            NotifyManager.getNotifierForSystem().notifyUser(serverApplicationVersion);
        } else {
            log.warn("Update is available, but there is no version for current system: {}", serverApplicationVersion);
        }
    }
}


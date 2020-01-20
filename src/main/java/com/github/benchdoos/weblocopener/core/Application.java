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

package com.github.benchdoos.weblocopener.core;

import com.github.benchdoos.jcolorful.core.JColorful;
import com.github.benchdoos.weblocopener.Main;
import com.github.benchdoos.weblocopener.gui.UpdateDialog;
import com.github.benchdoos.weblocopener.nongui.NonGuiUpdater;
import com.github.benchdoos.weblocopener.utils.CleanManager;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopenercore.core.constants.ApplicationArgument;
import com.github.benchdoos.weblocopenercore.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopenercore.preferences.PreferencesManager;
import com.github.benchdoos.weblocopenercore.utils.Internal;
import com.github.benchdoos.weblocopenercore.utils.browser.BrowserManager;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

@Log4j2
public class Application {
    public static UPDATE_MODE updateMode = UPDATE_MODE.NORMAL;


    public Application(final String[] args) {
        log.info("{} starts in mode: {}", ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME, Main.getCurrentMode());
        log.info("{} starts with arguments: {}", ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME, Arrays.toString(args));

        BrowserManager.loadBrowserList();

        CoreUtils.enableLookAndFeel();

        manageArgumentsForNew(args);
    }

    public static String getApplicationPath() {
        String canonicalPath = null;
        try {
            canonicalPath = new File(Application.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();
        } catch (URISyntaxException e) {
            log.debug("Can not get canonical path for settings", e);
        }
        return canonicalPath;
    }

    private void manageArgumentsForNew(String[] args) {
        if (args.length == 0) {
            startSettingsWithUpdate();
        } else {
            final String argument = args[0];

            final ApplicationArgument applicationArgument = ApplicationArgument.getByArgument(argument);
            if (applicationArgument != null) {
                switch (applicationArgument) {
                    case OPENER_SETTINGS_ARGUMENT:
                        CleanManager.clean();
                        startSettingsWithUpdate();
                        break;
                    case OPENER_UPDATE_ARGUMENT:
                        runUpdateDialog();
                        break;
                    case UPDATE_SILENT_ARGUMENT:
                        checkIfUpdatesAvailable();
                        break;
                    default:
                        cleanAndLoadCore(args);
                }
            } else {
                cleanAndLoadCore(args);
            }
        }
    }

    private void cleanAndLoadCore(String[] args) {
        CleanManager.clean();

        com.github.benchdoos.weblocopenercore.core.Application.manageArguments(args);
    }

    private void startSettingsWithUpdate() {
        String canonicalPath = getApplicationPath();
        checkIfUpdatesAvailable();
        com.github.benchdoos.weblocopenercore.core.Application.runSettingsDialog(canonicalPath);
    }

    public static void runUpdateDialog() {
        final UpdateDialog updateDialog;

        if (PreferencesManager.isDarkModeEnabledNow()) {
            JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
            colorful.colorizeGlobal();
            updateDialog = UpdateDialog.getInstance();
            colorful.colorize(updateDialog);
        } else {
            updateDialog = UpdateDialog.getInstance();
        }

        updateDialog.setVisible(true);
        new Thread(updateDialog::checkForUpdates).start();
    }

    private static void runUpdateSilent() {
        updateMode = UPDATE_MODE.SILENT;
        boolean isAutoUpdate = PreferencesManager.isAutoUpdateActive();

        log.debug(PreferencesManager.KEY_AUTO_UPDATE + " : " + isAutoUpdate);
        if (isAutoUpdate) {
            new NonGuiUpdater();
        }
    }

    private static void checkIfUpdatesAvailable() {
        log.debug("Checking if updates available");
        if (Internal.isCurrentTimeOlder(PreferencesManager.getLatestUpdateCheck(), 24)) {
            log.info("Checking if updates are available now, last check was at: {}", PreferencesManager.getLatestUpdateCheck());
            Thread checker = new Thread(Application::runUpdateSilent);
            checker.start();
        } else {
            log.info("Updates were checked less then 24h ago");
        }
    }

    public enum UPDATE_MODE {NORMAL, SILENT}
}

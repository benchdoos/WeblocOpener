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

import com.github.benchdoos.weblocopener.Main;
import com.github.benchdoos.weblocopener.gui.UpdateDialog;
import com.github.benchdoos.weblocopener.nongui.NonGuiUpdater;
import com.github.benchdoos.weblocopener.utils.CleanManager;
import com.github.benchdoos.weblocopenercore.constants.ApplicationArgument;
import com.github.benchdoos.weblocopenercore.constants.ApplicationConstants;
import com.github.benchdoos.weblocopenercore.domain.preferences.DevModeFeatureType;
import com.github.benchdoos.weblocopenercore.service.WindowLauncher;
import com.github.benchdoos.weblocopenercore.service.application.ApplicationService;
import com.github.benchdoos.weblocopenercore.service.application.impl.DefaultApplicationService;
import com.github.benchdoos.weblocopenercore.service.settings.dev_mode.DevModeFeatureCheck;
import com.github.benchdoos.weblocopenercore.service.settings.impl.AutoUpdateSettings;
import com.github.benchdoos.weblocopenercore.service.settings.impl.LatestUpdateCheckSettings;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Application {
  public static UPDATE_MODE updateMode = UPDATE_MODE.NORMAL;
  private final ApplicationService applicationService = new DefaultApplicationService();

  public Application(final String[] args) {
    log.info(
        "{} starts in mode: {}",
        ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
        Main.getCurrentMode());
    log.info(
        "{} starts with arguments: {}",
        ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
        Arrays.toString(args));

    manageArgumentsForNew(args);
  }

  public static void runUpdateDialog() {
    final UpdateDialog updateDialog =
        new WindowLauncher<UpdateDialog>() {
          @Override
          public UpdateDialog initWindow() {
            return new UpdateDialog();
          }
        }.getWindow();

    updateDialog.setVisible(true);
    new Thread(updateDialog::checkForUpdates).start();
  }

  private static void runUpdateSilent() {
    updateMode = UPDATE_MODE.SILENT;
    boolean isAutoUpdate = new AutoUpdateSettings().getValue();

    log.debug("Auto update enabled: {}", isAutoUpdate);
    if (isAutoUpdate) {
      new NonGuiUpdater();
    }
  }

  private static void checkIfUpdatesAvailable() {
    log.debug("Checking if updates available");
    final Date latestUpdateCheckDate = new LatestUpdateCheckSettings().getValue();

    final boolean mockedUpdateCheck =
        new DevModeFeatureCheck().isActive(DevModeFeatureType.UPDATER_LAST_CHECK_MOCK);
    if (mockedUpdateCheck) {
      log.warn("Last update check mocked! Ignoring current value: {}", latestUpdateCheckDate);
    }

    if (mockedUpdateCheck || lastCheckWasLaterThenADay(latestUpdateCheckDate)) {
      log.info(
          "Checking if updates are available now, last check was at: {}", latestUpdateCheckDate);
      Thread checker = new Thread(Application::runUpdateSilent);
      checker.start();
    } else {
      log.info("Updates were checked less then 24h ago");
    }
  }

  private static boolean lastCheckWasLaterThenADay(final Date latestUpdateCheckDate) {
    final LocalDateTime updateDate =
        LocalDateTime.ofInstant(latestUpdateCheckDate.toInstant(), ZoneId.systemDefault());
    return updateDate.plus(1, ChronoUnit.DAYS).isBefore(LocalDateTime.now());
  }

  private void manageArgumentsForNew(String[] args) {
    if (args.length == 0) {
      startSettingsWithUpdate();
    } else {
      final String argument = args[0];

      final ApplicationArgument applicationArgument = ApplicationArgument.getByArgument(argument);

      log.info("Argument found: {}", applicationArgument);

      if (applicationArgument != null) {
        switch (applicationArgument) {
          case OPENER_SETTINGS_ARGUMENT -> {
            CleanManager.clean();
            startSettingsWithUpdate();
          }
          case OPENER_UPDATE_ARGUMENT -> runUpdateDialog();
          case UPDATE_SILENT_ARGUMENT -> checkIfUpdatesAvailable();
          default -> cleanAndLoadCore(args);
        }
      } else {
        cleanAndLoadCore(args);
      }
    }
  }

  private void cleanAndLoadCore(String[] args) {
    CleanManager.clean();

    log.debug("Starting managing arguments via args: {}", Arrays.toString(args));
    final List<String> arguments =
        com.github.benchdoos.weblocopenercore.core.Application.prepareArguments(args);
    com.github.benchdoos.weblocopenercore.core.Application.manageArguments(
        arguments.toArray(new String[] {}));
  }

  private void startSettingsWithUpdate() {
    checkIfUpdatesAvailable();
    final File applicationFile = applicationService.getApplicationFile();
    final String applicationPath = applicationFile != null ? applicationFile.getAbsolutePath() : null;
    com.github.benchdoos.weblocopenercore.core.Application.runSettingsDialog(applicationPath);
  }

  public enum UPDATE_MODE {
    NORMAL,
    SILENT
  }
}

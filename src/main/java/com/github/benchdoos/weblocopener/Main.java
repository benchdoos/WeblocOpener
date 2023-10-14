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

package com.github.benchdoos.weblocopener;

import com.github.benchdoos.weblocopener.core.Application;
import com.github.benchdoos.weblocopenercore.constants.ApplicationArgument;
import com.github.benchdoos.weblocopenercore.constants.PathConstants;
import com.github.benchdoos.weblocopenercore.exceptions.InstalledJavaVersionIsNotSupported;
import com.github.benchdoos.weblocopenercore.exceptions.UnsupportedSystemException;
import com.github.benchdoos.weblocopenercore.handlers.impl.InstalledJavaVersionIsNotSupportedExceptionHandler;
import com.github.benchdoos.weblocopenercore.service.notification.NotificationManager;
import com.github.benchdoos.weblocopenercore.service.settings.impl.DevModeSettings;
import com.github.benchdoos.weblocopenercore.service.translation.Translation;
import com.github.benchdoos.weblocopenercore.utils.CoreUtils;
import com.github.benchdoos.weblocopenercore.utils.system.OS;
import com.github.benchdoos.weblocopenercore.utils.system.SystemUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

import static com.github.benchdoos.weblocopenercore.constants.ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME;

@Log4j2
public class Main {
  private static MODE currentMode;

  public static void main(String[] args) {
    new Main(args);
  }

  public static MODE getCurrentMode() {
    return currentMode;
  }

  public Main(String[] args) {
    log.info(WEBLOCOPENER_APPLICATION_NAME + " starting with args: " + Arrays.toString(args));
    log.debug("Dev mode: {}", new DevModeSettings().getValue());

    currentMode = manageMode(args);
    try {
      log.info("Logging to: {}", PathConstants.APP_LOG_FOLDER_PATH);
      new CoreUtils().enableLookAndFeel();

      log.info("Current mode: {}", currentMode);
      SystemUtils.checkIfSystemIsSupported();

      CoreUtils.initBrowserList();

      new Application(args);
    } catch (UnsupportedSystemException e) {
      log.fatal("System not supported", e);
      final String translatedString = Translation.get("CommonsBundle", "systemNotSupported");
      final String message = translatedString + " " + OS.getCurrentOS().name();

      NotificationManager.getForcedNotification(null)
          .setThrowable(e)
          .showErrorNotification(message, message);
    } catch (InstalledJavaVersionIsNotSupported e) {
      new InstalledJavaVersionIsNotSupportedExceptionHandler().handle(e);
    } catch (com.github.benchdoos.weblocopenercore.exceptions.FileDoesNotExistException e) {
      log.fatal("File not found for arguments: {}", Arrays.toString(args));
      final Translation translation = new Translation("WeblocOpenerCommonsBundle");
      String title = translation.get("unexpectedException");
      String message = translation.get("fileNotFoundException");
      NotificationManager.getForcedNotification(null)
          .setThrowable(e)
          .showErrorNotification(title, message);
    } catch (Exception e) {
      log.fatal("System exited with exception", e);
      String message = Translation.get("WeblocOpenerCommonsBundle", "unexpectedException");
      NotificationManager.getForcedNotification(null)
          .setThrowable(e)
          .showErrorNotification(message, message);
    }
  }

  private MODE manageMode(String[] args) {
    if (args.length > 0) {
      final String arg = args[0];

      final boolean updateArg =
          ApplicationArgument.OPENER_UPDATE_ARGUMENT.equals(ApplicationArgument.getByArgument(arg));
      if (updateArg) {
        return MODE.UPDATE;
      }
    }
    return MODE.WEBLOCOPENER;
  }

  enum MODE {
    WEBLOCOPENER,
    UPDATE
  }
}

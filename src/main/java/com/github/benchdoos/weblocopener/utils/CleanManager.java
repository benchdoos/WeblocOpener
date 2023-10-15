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

package com.github.benchdoos.weblocopener.utils;

import static com.github.benchdoos.weblocopener.core.ApplicationConstants.UPDATE_PATH_FILE;

import com.github.benchdoos.weblocopener.update.impl.UnixUpdater;
import com.github.benchdoos.weblocopener.update.impl.WindowsUpdater;
import com.github.benchdoos.weblocopenercore.utils.system.OS;
import java.io.File;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.util.Files;

@UtilityClass
@Log4j2
public class CleanManager {
  public static void clean() {
    File folder = new File(UPDATE_PATH_FILE);
    log.info("Cleaning: {}", folder);
    if (folder.isDirectory()) {
      final File[] files = folder.listFiles();
      if (files != null) {
        for (File file : files) {
          checkAndDeleteInstaller(file);
        }
      }
    }
  }

  private static void checkAndDeleteInstaller(final File file) {
    boolean toDelete = false;
    if (OS.isWindows()) {
      toDelete = file.getName().matches(WindowsUpdater.WINDOWS_FILE_REGEX);
    }

    if (OS.isUnix()) {
      toDelete = file.getName().matches(UnixUpdater.DEB_FILE_REGEX);
    }

    if (toDelete) {
      Files.delete(file);
      log.info("Setup file was deleted: {}", file);
    }
  }
}

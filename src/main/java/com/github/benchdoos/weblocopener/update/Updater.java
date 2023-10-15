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

package com.github.benchdoos.weblocopener.update;

import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.exceptions.NoAvailableVersionException;
import com.github.benchdoos.weblocopenercore.service.actions.ActionListenerSupport;
import java.io.File;
import java.io.IOException;

public interface Updater extends ActionListenerSupport {

  /**
   * Get installer file if downloading already started. Otherwise {@code null} will be returned.
   *
   * @return Installer file link
   */
  File getInstallerFile();

  /**
   * @return latest {@link AppVersion}, giving Beta if {@link
   *     com.github.benchdoos.weblocopenercore.service.settings.impl.InstallBetaUpdateSettings} is
   *     {@code true} and Release is older than Beta version, otherwise will return Release version.
   */
  AppVersion getLatestAppVersion();

  /**
   * @return latest Release {@link AppVersion}
   */
  AppVersion getLatestRelease();

  /**
   * @return latest Beta version {@link AppVersion}
   */
  AppVersion getLatestBeta();

  AppVersion.Asset getInstallerAsset(AppVersion appVersion) throws NoAvailableVersionException;

  void startUpdate(AppVersion applicationVersion) throws IOException;
}

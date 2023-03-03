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

package com.github.benchdoos.weblocopener.update.impl;

import com.github.benchdoos.weblocopener.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.service.UpdateService;
import com.github.benchdoos.weblocopener.service.impl.DefaultUpdateService;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.utils.FileDownloader;
import com.github.benchdoos.weblocopener.utils.UpdateHelperUtil;
import com.github.benchdoos.weblocopenercore.client.GitHubClient;
import com.github.benchdoos.weblocopenercore.client.impl.DefaultGitHubClient;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.exceptions.NoAvailableVersionException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class WindowsUpdater implements Updater {
  public static final String WINDOWS_FILE_REGEX = ".*WeblocOpener.*\\.exe";
  private static final Object RELEASE_MUTEX = new Object();
  private static final Object BETA_MUTEX = new Object();
  private static AtomicReference<AppVersion> latestReleaseVersion = null;
  private static AtomicReference<AppVersion> latestBetaVersion = null;
  final List<com.github.benchdoos.weblocopenercore.service.actions.ActionListener<Integer>>
      listeners = new CopyOnWriteArrayList<>();
  private final GitHubClient gitHubClient = new DefaultGitHubClient();
  private final UpdateService updateService;
  @Getter private File installerFile = null;

  public WindowsUpdater() {
    updateService = new DefaultUpdateService(this);
  }

  @Override
  public void addListener(
      final com.github.benchdoos.weblocopenercore.service.actions.ActionListener actionListener) {
    listeners.add(actionListener);
  }

  @Override
  public void removeListener(
      final com.github.benchdoos.weblocopenercore.service.actions.ActionListener actionListener) {
    listeners.remove(actionListener);
  }

  @Override
  public void removeAllListeners() {
    listeners.clear();
  }

  @Override
  public AppVersion getLatestAppVersion() {
    return updateService.getLatest();
  }

  @Override
  public AppVersion getLatestRelease() {
    if (latestReleaseVersion != null) {
      return latestReleaseVersion.get();
    }

    synchronized (RELEASE_MUTEX) {
      final AppVersion latestRelease = gitHubClient.getLatestRelease();

      latestReleaseVersion = new AtomicReference<>(latestRelease);

      return latestReleaseVersion.get();
    }
  }

  @Override
  public AppVersion getLatestBeta() {
    if (latestBetaVersion != null) {
      return latestBetaVersion.get();
    }

    synchronized (BETA_MUTEX) {
      final AppVersion latestBetaRelease = gitHubClient.getLatestBetaRelease();
      latestBetaVersion = new AtomicReference<>(latestBetaRelease);

      return latestBetaVersion.get();
    }
  }

  @Override
  public AppVersion.Asset getInstallerAsset(final AppVersion appVersion)
      throws NoAvailableVersionException {
    if (appVersion == null) {
      throw new NoAvailableVersionException("Given AppVersion is null");
    }

    if (CollectionUtils.isNotEmpty(appVersion.assets())) {
      return appVersion.assets().stream()
          .filter(
              a ->
                  a.contentType().equals("application/octet-stream")
                      && a.name().matches(WINDOWS_FILE_REGEX))
          .findFirst()
          .orElseThrow(() -> new NoAvailableVersionException("Needed installer file not found"));
    }

    throw new NoAvailableVersionException("Given AppVersion assets are empty");
  }

  @Override
  public void startUpdate(AppVersion appVersion) throws IOException {
    log.info("Starting update for {}", appVersion.version());
    final AppVersion.Asset installerAsset = this.getInstallerAsset(appVersion);

    installerFile =
        new File(
            ApplicationConstants.UPDATE_PATH_FILE
                + UpdateHelperUtil.getUpdatePrefix(appVersion.version())
                + installerAsset.name());
    log.debug("Installer file: {} exists: {}", installerFile, installerFile.exists());

    final FileDownloader fileDownloader =
        new FileDownloader(installerAsset.downloadUrl(), installerFile);

    try {
      if (!installerFile.exists() || installerFile.length() != installerAsset.size()) {
        fileDownloader.setTotalFileSize(installerAsset.size());
        listeners.forEach(fileDownloader::addListener);
        fileDownloader.download();
      }

      if (!Thread.currentThread().isInterrupted()) {
        log.debug("Installer file: {} (size:{})", installerFile, installerFile.length());
        update(installerFile);
      }
    } catch (IOException e) {
      log.warn("Can not download file: {} to {}", installerAsset.downloadUrl(), installerFile, e);

      log.debug("Setting file: {} to be deleted on app exit", installerFile);
      installerFile.deleteOnExit();
      throw new IOException(e);
    } catch (InterruptedException e) {
      log.warn(
          "Downloading file {} from {} was interrupted.",
          installerFile,
          installerAsset.downloadUrl(),
          e);
      this.removeAllListeners();
      Thread.currentThread().interrupt();
    } finally {
      fileDownloader.removeAllListeners();
    }
  }

  private void update(File file) throws IOException {
    final String command = file.getAbsolutePath() + " " + ApplicationConstants.INSTALLER_SILENT_KEY;
    log.debug("Starting update with command: [{}]", command);

    Runtime.getRuntime().exec(command);
    log.debug("Exiting app...");
    System.exit(0);
  }
}

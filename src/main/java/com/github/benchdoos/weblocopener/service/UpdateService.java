package com.github.benchdoos.weblocopener.service;

import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.service.settings.impl.InstallBetaUpdateSettings;

/** Update service */
public interface UpdateService {

  /**
   * Get latest version if beta is {@link InstallBetaUpdateSettings#getValue()} returns true;
   *
   * @return latest version
   */
  AppVersion getLatest();

  AppVersion.Asset getInstallerAsset(AppVersion version);
}

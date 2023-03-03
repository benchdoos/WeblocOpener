package com.github.benchdoos.weblocopener.service.impl;

import com.github.benchdoos.weblocopener.service.UpdateService;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.exceptions.NoAvailableVersionException;
import com.github.benchdoos.weblocopenercore.service.settings.impl.InstallBetaUpdateSettings;
import com.github.benchdoos.weblocopenercore.service.settings.impl.LatestUpdateCheckSettings;
import com.github.benchdoos.weblocopenercore.utils.VersionUtils;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class DefaultUpdateService implements UpdateService {

  private final Updater updater;

  @Override
  public AppVersion getLatest() {

    new LatestUpdateCheckSettings().save(new Date());

    if (Boolean.TRUE.equals(new InstallBetaUpdateSettings().getValue())) {
      log.debug("Install beta is enabled checking what version is newer");

      try {
        final CompletableFuture<AppVersion> releaseFuture =
            new CompletableFuture<AppVersion>().completeAsync(updater::getLatestRelease);
        final CompletableFuture<AppVersion> betaFuture =
            new CompletableFuture<AppVersion>().completeAsync(updater::getLatestBeta);

        final AppVersion release = releaseFuture.get();
        final AppVersion beta = betaFuture.get();

        final VersionUtils.VersionCompare versionCompare =
            VersionUtils.versionCompare(release, beta);

        switch (versionCompare) {
          case FIRST_VERSION_IS_NEWER -> {
            return release;
          }
          case SECOND_VERSION_IS_NEWER -> {
            return beta;
          }
          default -> {
            log.warn(
                "This is unreachable, VersionCompare: {}, but returning value release: {}",
                versionCompare,
                release);
            return release;
          }
        }

      } catch (Exception e) {
        log.warn("Could not get latest version", e);
        return updater.getLatestRelease();
      }

    } else {
      log.debug("Install beta is disabled, returning latest release");
      return updater.getLatestRelease();
    }
  }

  @Override
  public AppVersion.Asset getInstallerAsset(final AppVersion version)
      throws NoAvailableVersionException {
    if (version == null) {
      throw new NoAvailableVersionException("Giver version is null");
    }

    return updater.getInstallerAsset(version);
  }
}

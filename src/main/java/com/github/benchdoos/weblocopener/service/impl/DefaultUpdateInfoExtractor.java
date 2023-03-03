package com.github.benchdoos.weblocopener.service.impl;

import com.github.benchdoos.weblocopener.service.UpdateInfoExtractor;
import com.github.benchdoos.weblocopener.utils.UpdateHelperUtil;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Optional;

@Log4j2
public class DefaultUpdateInfoExtractor implements UpdateInfoExtractor {

  @Override
  public UpdateInfo extract(final AppVersion appVersion) {
    final Optional<AppVersion.Asset> assetOptional =
        appVersion.assets().stream()
            .filter(a -> a.contentType().equalsIgnoreCase("application/json"))
            .filter(a -> a.name().startsWith("update-info") && a.name().endsWith(".json"))
            .findFirst();

    if (assetOptional.isPresent()) {
      final AppVersion.Asset asset = assetOptional.get();
      try {
        return UpdateHelperUtil.getUpdateInfoFromUrl(asset.downloadUrl());
      } catch (IOException e) {
        log.warn("Could not get UpdateInfo by url: {}", asset, e);
      }
    }

    log.warn("Could not get asset for update info, returning null");
    return null;
  }
}

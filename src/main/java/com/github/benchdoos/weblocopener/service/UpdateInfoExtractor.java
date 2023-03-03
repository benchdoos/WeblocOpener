package com.github.benchdoos.weblocopener.service;

import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;

public interface UpdateInfoExtractor {
  UpdateInfo extract(AppVersion appVersion);
}

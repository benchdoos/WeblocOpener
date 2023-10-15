package com.github.benchdoos.weblocopener.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.update.impl.UnixUpdater;
import com.github.benchdoos.weblocopener.update.impl.WindowsUpdater;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;
import com.github.benchdoos.weblocopenercore.service.actions.ActionListener;
import com.github.benchdoos.weblocopenercore.utils.system.OS;
import com.github.benchdoos.weblocopenercore.utils.version.Version;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.Timer;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;

@UtilityClass
@Log4j2
public class UpdateHelperUtil {

  public Updater getUpdaterForCurrentOS() {
    if (OS.isWindows()) {
      return new WindowsUpdater();
    } else if (OS.isUnix()) {
      return new UnixUpdater();
    } else {
      log.warn("For OS: {} returning: {}", OS.getCurrentOS(), UnixUpdater.class);
      return new UnixUpdater();
    }
  }

  public UpdateInfo getUpdateInfoFromUrl(final URL url) throws IOException {
    final String jsonSrc = IOUtils.toString(url, StandardCharsets.UTF_8);
    final ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(jsonSrc, UpdateInfo.class);
  }

  public Timer createNotifierTimer(
      final AppVersion.Asset asset, final File file, List<ActionListener<Integer>> listeners) {

    if (CollectionUtils.isNotEmpty(listeners)) {
      final long totalSize = asset.size();

      final Timer timer = new Timer(500, null);

      timer.addActionListener(
          e -> {
            final int value = Math.toIntExact(file.length());

            final Integer percent = (int) (((double) value / totalSize) * 100);

            log.debug("Downloaded: {}% ({} bites of total {} bites)", percent, value, totalSize);

            listeners.forEach(l -> l.actionPerformed(percent));

            if (file.length() == asset.size()) {
              timer.stop();
            }
          });

      timer.setRepeats(true);
      timer.start();
      return timer;
    }
    return null;
  }

  public String getUpdatePrefix(Version version) {
    return "V" + version.getSimpleVersion() + "_";
  }
}

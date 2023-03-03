package com.github.benchdoos.weblocopener.utils;

import com.github.benchdoos.weblocopenercore.service.actions.ActionListener;
import com.github.benchdoos.weblocopenercore.service.actions.ActionListenerSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@Setter
@Getter
@RequiredArgsConstructor
public class FileDownloader implements ActionListenerSupport {

  /** Initial buffer size. */
  private static final int BUFFER_SIZE = 8192;
  final List<ActionListener<Integer>> listeners = new CopyOnWriteArrayList<>();
  private final URL link;
  private final File file;
  private Long totalFileSize;

  public void download() throws IOException, InterruptedException {
    if (link == null || file == null) {
      throw new IllegalArgumentException(
          "Link or file are not specified! Link: " + link + ", file: " + file);
    }

    log.debug("Starting downloading: {} to {}", link, file);

    try (BufferedInputStream in = new BufferedInputStream(link.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      final byte[] dataBuffer = new byte[BUFFER_SIZE];
      int bytesRead;
      int prevStep = 0;

      if (totalFileSize != null) {
        log.debug("Downloaded: {}% ({} bites of total {} bites)", 0, file.length(), totalFileSize);
      }

      while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
        if (!Thread.currentThread().isInterrupted()) {
          fileOutputStream.write(dataBuffer, 0, bytesRead);
          if (totalFileSize != null) {
            final int percent = (int) (((double) file.length() / totalFileSize) * 100);
            if (percent % 10 == 0 && prevStep != percent) {
              log.debug(
                  "Downloaded: {}% ({} bites of total {} bites)",
                  percent, file.length(), totalFileSize);
              prevStep = percent;
            }
            listeners.forEach(l -> l.actionPerformed(percent));
          }
        } else {
          throw new InterruptedException(
              String.format("File download from: %s to %s was interrupted", link, file));
        }
      }
    }
  }

  @Override
  public void addListener(final ActionListener actionListener) {
    listeners.add(actionListener);
  }

  @Override
  public void removeListener(final ActionListener actionListener) {
    listeners.remove(actionListener);
  }

  @Override
  public void removeAllListeners() {
    listeners.clear();
  }
}

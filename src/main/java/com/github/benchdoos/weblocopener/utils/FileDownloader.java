package com.github.benchdoos.weblocopener.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class FileDownloader{



  public void downloadFile(final URL link, final File file) throws IOException, InterruptedException {

    if (link == null || file == null) {
      throw new IllegalArgumentException("Link or file are not specified! Link: " + link + ", file: " + file);
    }


    try (BufferedInputStream in = new BufferedInputStream(link.openStream());
         FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        if (!Thread.currentThread().isInterrupted()) {
          fileOutputStream.write(dataBuffer, 0, bytesRead);
        } else {
          throw new InterruptedException(String.format("File download from: %s to %s was interrupted", link, file));
        }
      }
    }
  }
}

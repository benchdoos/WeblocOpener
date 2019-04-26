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

package com.github.benchdoos.weblocopener.service.clipboard;

import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.Channels;

/**
 * This holy crap is because gnome does not support normal copying for java or otherwise
 */
public class UnixClipboardHelper extends Thread {
    private static BufferedReader stdInCh = new BufferedReader(
            new InputStreamReader(Channels.newInputStream((
                    new FileInputStream(FileDescriptor.in)).getChannel())));
    final InputStream in = System.in;
    private final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    private final String text;
    private final BufferedImage image;

    UnixClipboardHelper(String text) {
        this.text = text;
        this.image = null;
    }

    UnixClipboardHelper(BufferedImage image) {
        this.text = null;
        this.image = image;
    }

    @Override
    public void run() {
        try {
            if (text != null) {
                copy(text);
            }
            if (image != null) {
                copy(image);
            }
        } catch (IOException e) {
            log.warn("Can not copy or something is wrong, but I guess it's ok!", e);
        }
        super.run();
    }

    @Override
    public void interrupt() {
        System.out.print('M');
        super.interrupt();
    }

    private void copy(BufferedImage image) throws IOException {
        CopyImageToClipBoard ci = new CopyImageToClipBoard();
        ci.copyImage(image);
        stdInCh.read();
    }

    private void copy(String text) throws IOException {
        StringSelection stringSelection = new StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        stdInCh.read();
    }
}

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

import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.image.BufferedImage;

@Log4j2
public class UnixClipboard implements Clipboard {
    private static final int TIMER_ALIVE = 60_000; //60 secs

    @Override
    public void copy(String string) {
        if (string != null) {
            UnixClipboardHelper thread = new UnixClipboardHelper(string);
            thread.start();
            interrupt(thread);
        }
    }

    private void interrupt(UnixClipboardHelper thread) {
        log.info("Copy thread {} will be interrupted in {} sec.", thread.getName(), TIMER_ALIVE / 1000);
        Timer timer = new Timer(TIMER_ALIVE, e -> {
            log.debug("Closing copy thread: {}", thread.getName());
            thread.interrupt();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void copy(BufferedImage image) {
        if (image != null) {
            UnixClipboardHelper thread = new UnixClipboardHelper(image);
            thread.start();

            interrupt(thread);
        }
    }
}

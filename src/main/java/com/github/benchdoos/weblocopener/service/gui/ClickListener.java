/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
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

package com.github.benchdoos.weblocopener.service.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("ALL")
public class ClickListener extends MouseAdapter implements ActionListener {
    private final static int clickInterval = (Integer) Toolkit.getDefaultToolkit().
            getDesktopProperty("awt.multiClickInterval");
    private final Timer timer;
    private MouseEvent lastEvent;

    public ClickListener() {
        this(clickInterval);
    }

    private ClickListener(int delay) {
        timer = new Timer(delay, this);
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 2) return;

        lastEvent = e;

        if (timer.isRunning()) {
            timer.stop();
            doubleClick(lastEvent);
        } else {
            timer.restart();
        }
    }

    public void actionPerformed(ActionEvent e) {
        timer.stop();
        singleClick(lastEvent);
    }

    private void singleClick(MouseEvent e) {
    }

    public void doubleClick(MouseEvent e) {
    }

}
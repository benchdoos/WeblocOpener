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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Created by Eugene Zrazhevsky on 025 25.09.2017.
 */
public class MousePickListener {
    final Point[] initialClick = new Point[1];
    Window parent = null;

    public final MouseAdapter getMouseAdapter = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            initialClick[0] = e.getPoint();
            parent.getComponentAt(initialClick[0]);
        }
    };
    public final MouseMotionAdapter getMouseMotionAdapter = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {

            // get location of Window
            int thisX = parent.getLocation().x;
            int thisY = parent.getLocation().y;

            // Determine how much the mouse moved since the initial click
            int xMoved = (thisX + e.getX()) - (thisX + initialClick[0].x);
            int yMoved = (thisY + e.getY()) - (thisY + initialClick[0].y);

            // Move window to this position
            int X = thisX + xMoved;
            int Y = thisY + yMoved;
            parent.setLocation(X, Y);
        }
    };

    public MousePickListener(Window parent) {
        this.parent = parent;
    }
}

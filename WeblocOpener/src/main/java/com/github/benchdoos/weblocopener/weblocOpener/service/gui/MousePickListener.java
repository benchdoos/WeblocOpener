package com.github.benchdoos.weblocopener.weblocOpener.service.gui;

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

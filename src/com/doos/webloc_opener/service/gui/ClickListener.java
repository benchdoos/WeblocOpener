package com.doos.webloc_opener.service.gui;

/**
 * Created by Eugene Zrazhevsky on 01.11.2016.
 */

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
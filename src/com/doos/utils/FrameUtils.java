package com.doos.utils;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.doos.service.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class FrameUtils {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    private static final Timer timer = new Timer(60, null);

    public static Point setFrameOnCenter(Dimension size) {
        int width = (int) ((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (size.getWidth() / 2));
        int height = (int) ((Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (size.getHeight() / 2));
        return new Point(width, height);
    }

    private static Window findWindow(Component c) {
        if (c == null) {
            return JOptionPane.getRootFrame();
        } else if (c instanceof Window) {
            return (Window) c;
        } else {
            return findWindow(c.getParent());
        }
    }

    public static void shakeFrame(final Component component) {
        final Window window = findWindow(component);

        if (!timer.isRunning()) {
            timer.addActionListener(new ActionListener() {
                final Point location = window.getLocation();
                final int maxCounter = 6;
                int counter = 0;
                int step = 14;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (counter <= 2) {
                        step = 14;
                    } else if (counter > 2 && counter <= 4) {
                        step = 7;
                    } else if (counter > 4 && counter <= 6) {
                        step = 3;
                    }

                    if (counter <= maxCounter) {
                        counter++;
                        if (counter % 2 == 1) {
                            Point newLocation = new Point(location.x + step, location.y);
                            window.setLocation(newLocation);
                        } else {
                            Point newLocation = new Point(location.x - step, location.y);
                            window.setLocation(newLocation);
                        }
                    } else {
                        Point newLocation = new Point(location.x, location.y);
                        window.setLocation(newLocation);

                        counter = 0;
                        timer.removeActionListener(timer.getActionListeners()[0]);
                        timer.stop();
                    }
                }
            });
            timer.start();
        }
        Toolkit.getDefaultToolkit().beep();
    }
}

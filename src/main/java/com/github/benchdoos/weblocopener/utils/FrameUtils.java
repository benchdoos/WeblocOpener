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

package com.github.benchdoos.weblocopener.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class FrameUtils {
    private static final Timer timer = new Timer(60, null);

    /**
     * Returns the location of point of window, when it should be on center of the screen.
     *
     * @return Point of <code>Window</code> that is moved to center of the screen.
     * @see Component#getLocation
     */
    public static Point getFrameOnCenterLocationPoint(Window window) {
        Dimension size = window.getSize();
        int width = (int) ((Toolkit.getDefaultToolkit().getScreenSize().width / (double) 2) - (size.getWidth() / (double) 2));
        int height = (int) ((Toolkit.getDefaultToolkit().getScreenSize().height / (double) 2) - (size.getHeight() / (double) 2));
        return new Point(width, height);
    }

    /**
     * Finds window on component given.
     *
     * @param component Component where window is located.
     * @return Window that is searched.
     **/
    public static Window findWindow(Component component) {
        if (component == null) {
            return JOptionPane.getRootFrame();
        } else if (component instanceof Window) {
            return (Window) component;
        } else {
            return findWindow(component.getParent());
        }
    }

    /**
     * Finds window on component given.
     *
     * @param component Component where window is located.
     * @return Window that is searched.
     **/
    public static Dialog findDialog(Component component) {
        if (component == null) {
            return null;
        } else if (component instanceof Dialog) {
            return (Dialog) component;
        } else {
            return findDialog(component.getParent());
        }
    }

    /**
     * Shakes window like in MacOS.
     *
     * @param component Component to shake
     */
    static void shakeFrame(final Component component) {
        final Window window = findWindow(component);

        if (!timer.isRunning()) {
            timer.addActionListener(new ActionListener() {
                final static int maxCounter = 6;
                Point location = window.getLocation();
                int counter = 0;
                int step = 14;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (counter <= 2) {
                        step = 14;
                    } else if (counter <= 4) {
                        step = 7;
                    } else if (counter <= 6) {
                        step = 3;
                    }

                    if (counter >= 0) {
                        if (counter <= maxCounter) {
                            counter++;

                            if (location.x < 0 || location.y < 0) {
                                window.setLocation(getFrameOnCenterLocationPoint(window));
                                location = window.getLocation();
                            }
                            if (counter % 2 != 0) {
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
                }
            });
            timer.start();
        }
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Brings windows to front. If some application is already in use, window is shown on top without focus.
     * If user requests switch focus to window and goes back, window will hide.
     *
     * @param window Frame to make on the front of the screen.
     */
    public static void showOnTop(Window window) {
        EventQueue.invokeLater(() -> {
            window.setAlwaysOnTop(true);
            window.toFront();
            window.repaint();
            window.setAlwaysOnTop(false);
        });
    }
}

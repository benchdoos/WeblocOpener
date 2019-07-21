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

import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
@Log4j2
public class FrameUtils {
    private static final Timer timer = new Timer(60, null);

    /**
     * Returns the location of point of window, when it should be on center of the screen.
     *
     * @return Point of <code>Window</code> that is moved to center of the screen.
     * @see Component#getLocation
     */
    public static Point getFrameOnCenterLocationPoint(Window window) {
        final Dimension size = window.getSize();
        int width = (int) ((Toolkit.getDefaultToolkit().getScreenSize().width / (double) 2) - (size.getWidth() / (double) 2));
        int height = (int) ((Toolkit.getDefaultToolkit().getScreenSize().height / (double) 2) - (size.getHeight() / (double) 2));
        return new Point(width, height);
    }

    /**
     * Returns the location of point of window, when it should be on center of the parent window.
     *
     * @param parent window
     * @param window that should be in center of the parent window
     * @return Point of <code>Window</code> that is moved to center of the screen.
     * @see Component#getLocation()
     */
    public static Point getFrameOnCenterOfParentFrame(Window parent, Window window) {
        final Dimension size = window.getSize();
        int width = (int) ((parent.getSize().width / (double) 2) - (size.getWidth() / (double) 2));
        int height = (int) ((parent.getSize().height / (double) 2) - (size.getHeight() / (double) 2));

        return new Point(width + parent.getLocation().x, height + parent.getLocation().y);
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
    public static void shakeFrame(final Component component) {
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

    public static KeyAdapter getSmartKeyAdapter(Component component) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (component != null) {
                        component.dispatchEvent(e);
                    }
                }
            }
        };
    }

    public static void fillTextFieldWithClipboard(JTextField textField) {
        String data = "<empty clipboard>";
        try {
            data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            URL url = new URL(data);
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(data)) {
                textField.setText(url.toString());
                setTextFieldFont(textField, textField.getFont(), TextAttribute.UNDERLINE_ON);
                textField.setCaretPosition(textField.getText().length());
                textField.selectAll();
                log.debug("Got URL from clipboard: " + url);
            }
        } catch (UnsupportedFlavorException | IllegalStateException | HeadlessException | IOException e) {
            textField.setText("");
            log.warn("Can not read URL from clipboard: [" + data + "]", e);
        }
    }

    public static void setTextFieldFont(JTextField textField, Font font, int attribute2) {
        Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.UNDERLINE, attribute2);
        textField.setFont(font.deriveFont(fontAttributes));
    }

    public static String getFilePathFromFileDialog(FileDialog fileDialog, Logger log) {
        fileDialog.setVisible(true);
        File[] f = fileDialog.getFiles();
        if (f.length > 0) {
            final String absolutePath = fileDialog.getFiles()[0].getAbsolutePath();
            log.debug("Choice: " + absolutePath);
            fileDialog.dispose();
            return absolutePath;
        } else {
            log.debug("Choice canceled");
            fileDialog.dispose();
            return null;
        }
    }
}

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

package com.github.benchdoos.weblocopener.gui.panels;

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.gui.PlaceholderTextField;
import com.github.benchdoos.weblocopener.gui.Translatable;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.service.links.Link;
import com.github.benchdoos.weblocopener.utils.FileUtils;
import com.github.benchdoos.weblocopener.utils.FrameUtils;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.notification.NotificationManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateNewFilePanel extends JPanel implements Translatable {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    private Window parentWindow;

    private JPanel contentPane;

    private JButton buttonSave;
    private JButton buttonCancel;
    private JTextField urlTextField;

    public CreateNewFilePanel() {
        $$$setupUI$$$();
        initGui();
    }

    public void initGui() {
        initActionListeners();

        initWindowGui();

        translate();
    }

    public void setParentWindow(Window parentWindow) {
        this.parentWindow = parentWindow;
    }

    private void initActionListeners() {
        buttonSave.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());
    }

    private void initWindowGui() {
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public JTextField getUrlTextField() {
        return urlTextField;
    }

    private void onOK() {
        final String text = urlTextField.getText();
        try {
            final URL url = new URL(text);
            String path = saveFileBrowser();
            final Link link = PreferencesManager.getLink();

            if (path != null) {
                final String suffix = "." + link.getExtension();
                if (!path.endsWith(suffix)) {
                    path += suffix;
                }
                try {
                    log.debug("Link with url: {} at location: {} will be created as: {}", url, path, link.getClass().getSimpleName());

                    link.createLink(new File(path), url);

                    if (PreferencesManager.openFolderForNewFile()) {
                        FileUtils.openFileInFileBrowser(new File(path));
                    } else {
                        log.info("Opening in file browser disabled by settings");
                    }

                    parentWindow.dispose();
                } catch (IOException e) {
                    log.warn("Could not create .webloc link at: {} with url: {}", path, url, e);
                    NotificationManager.getNotificationForCurrentOS().showErrorNotification(
                            ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME,
                            Translation.getTranslatedString("CreateNewFileBundle", "errorSave")
                                    + " " + new File(path).getName() + " \n" + e.getLocalizedMessage()
                    );
                }
            }
        } catch (MalformedURLException e) {
            log.warn("Could not create url from text: {}, cause: {}", text, e.getMessage());
            FrameUtils.shakeFrame(this);
        }
    }

    private void onCancel() {
        parentWindow.dispose();
    }

    private String saveFileBrowser() {
        log.debug("Opening File Browser");

        FileDialog fileDialog = new FileDialog(FrameUtils.findDialog(this),
                Translation.getTranslatedString("CreateNewFileBundle", "saveAsFile"),
                FileDialog.SAVE);
        try {
            fileDialog.setIconImage(Toolkit.getDefaultToolkit()
                    .getImage(getClass().getResource("/images/balloonIcon256.png")));
            final String property = System.getProperty("user.home");
            fileDialog.setDirectory(property);
            return FrameUtils.getFilePathFromFileDialog(fileDialog, log);
        } catch (Exception e) {
            log.warn("Could not launch File Browser", e);
            fileDialog.dispose();
            return null;
        }
    }

    private void createUIComponents() {
        urlTextField = new PlaceholderTextField();
        ((PlaceholderTextField) urlTextField).setPlaceholder("URL");
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonSave = new JButton();
        this.$$$loadButtonText$$$(buttonSave, ResourceBundle.getBundle("translations/CreateNewFileBundle").getString("saveButton"));
        panel2.add(buttonSave, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, ResourceBundle.getBundle("translations/CommonsBundle").getString("cancelButton"));
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.add(urlTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(400, -1), new Dimension(400, -1), new Dimension(400, -1), 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    @Override
    public void translate() {
        buttonSave.setText(Translation.getTranslatedString("CreateNewFileBundle", "saveButton"));
        buttonCancel.setText(Translation.getTranslatedString("CommonsBundle", "cancelButton"));
    }

    public JButton getOkButton() {
        return buttonSave;
    }
}

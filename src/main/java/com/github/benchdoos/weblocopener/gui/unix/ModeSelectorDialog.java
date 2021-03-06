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

package com.github.benchdoos.weblocopener.gui.unix;

import com.github.benchdoos.jcolorful.core.JColorful;
import com.github.benchdoos.weblocopener.core.Application;
import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.core.constants.ArgumentConstants;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.gui.Translatable;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.utils.FrameUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;

import static com.github.benchdoos.weblocopener.core.constants.ArgumentConstants.*;

@Log4j2
public class ModeSelectorDialog extends JFrame implements Translatable {
    private final File file;
    private String mode = PreferencesManager.getUnixOpeningMode();
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton openRadioButton;
    private JRadioButton editRadioButton;
    private JCheckBox saveSelectionCheckBox;
    private JRadioButton copyRadioButton;
    private JRadioButton generateQrRadioButton;
    private JRadioButton copyQrRadioButton;
    private JLabel windowTitleLabel;
    private ButtonGroup buttonGroup;

    public ModeSelectorDialog(File file) {
        this.file = file;
        initGui();

    }

    public void initGui() {
        setContentPane(contentPane);
        setTitle(file.getName());
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/balloonIcon256.png")));

        colorizeThis();

        getRootPane().setDefaultButton(buttonOK);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initActionListeners();

        initButtonGroup();

        initSelectionMode();

        translate();

        pack();
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setResizable(false);
    }

    private void initSelectionMode() {
        if (mode.equalsIgnoreCase(SettingsConstants.OPENER_UNIX_DEFAULT_SELECTOR_MODE)) {
            mode = ArgumentConstants.OPENER_OPEN_ARGUMENT;
        }

        switch (mode) {
            case OPENER_EDIT_ARGUMENT:
                editRadioButton.setSelected(true);
                break;
            case OPENER_QR_ARGUMENT:
                generateQrRadioButton.setSelected(true);
                break;
            case OPENER_COPY_LINK_ARGUMENT:
                copyRadioButton.setSelected(true);
                break;
            case OPENER_COPY_QR_ARGUMENT:
                copyQrRadioButton.setSelected(true);
                break;
            default:
                openRadioButton.setSelected(true);
                break;

        }
    }

    private void initButtonGroup() {
        buttonGroup = new ButtonGroup();
        buttonGroup.add(openRadioButton);
        buttonGroup.add(editRadioButton);
        buttonGroup.add(copyRadioButton);
        buttonGroup.add(generateQrRadioButton);
        buttonGroup.add(copyQrRadioButton);
    }

    private void colorizeThis() {
        if (PreferencesManager.isDarkModeEnabledNow()) {
            final JColorful colorful = new JColorful(ApplicationConstants.DARK_MODE_THEME);
            colorful.colorize(this);
        }
    }

    private void initActionListeners() {
        buttonOK.addActionListener(e -> onOk());
        buttonCancel.addActionListener(e -> onCancel());

        rootPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(
                KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        rootPane.registerKeyboardAction(e -> onSelectionMove(-1), KeyStroke.getKeyStroke(
                KeyEvent.VK_UP, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
        rootPane.registerKeyboardAction(e -> onSelectionMove(1), KeyStroke.getKeyStroke(
                KeyEvent.VK_DOWN, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );


        openRadioButton.addItemListener(getRadioButtonListener(OPENER_OPEN_ARGUMENT));
        editRadioButton.addItemListener(getRadioButtonListener(OPENER_EDIT_ARGUMENT));
        copyRadioButton.addItemListener(getRadioButtonListener(OPENER_COPY_LINK_ARGUMENT));
        generateQrRadioButton.addItemListener(getRadioButtonListener(OPENER_QR_ARGUMENT));
        copyQrRadioButton.addItemListener(getRadioButtonListener(OPENER_COPY_QR_ARGUMENT));
    }

    private ItemListener getRadioButtonListener(String mode) {
        return e -> {
            if (((JRadioButton) e.getItem()).isSelected()) {
                this.mode = mode;
            }
        };
    }

    private void onSelectionMove(int move) {
        final Enumeration<AbstractButton> elements = buttonGroup.getElements();
        ArrayList<JRadioButton> buttons = new ArrayList<>();
        while (elements.hasMoreElements()) {
            final AbstractButton abstractButton = elements.nextElement();
            buttons.add(((JRadioButton) abstractButton));
        }

        for (int i = 0; i < buttons.size(); i++) {
            JRadioButton current = buttons.get(i);
            if (current.isSelected()) {
                final int nextIndex = i + move;
                if (nextIndex >= 0) {
                    if (nextIndex < buttons.size()) {
                        buttons.get(nextIndex).setSelected(true);
                    } else {
                        buttons.get(0).setSelected(true);
                    }
                } else {
                    buttons.get(buttons.size() - 1).setSelected(true);
                }
                break;
            }
        }
    }

    private void onCancel() {
        dispose();
    }

    private void onOk() {
        if (saveSelectionCheckBox.isSelected()) {
            PreferencesManager.setUnixOpeningMode(mode);
            PreferencesManager.flushPreferences();
        }
        if (!mode.equalsIgnoreCase(SettingsConstants.OPENER_UNIX_DEFAULT_SELECTOR_MODE)) {
            log.info("Starting processing of file: {} in mode: {}", file, mode);
            String[] args = new String[]{mode, file.getAbsolutePath()};
            Application.manageArguments(args);
            dispose();
        } else {
            FrameUtils.shakeFrame(this);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
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
        buttonOK = new JButton();
        this.$$$loadButtonText$$$(buttonOK, ResourceBundle.getBundle("translations/CommonsBundle").getString("okButton"));
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, ResourceBundle.getBundle("translations/CommonsBundle").getString("cancelButton"));
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel3.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        saveSelectionCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(saveSelectionCheckBox, ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("saveCheckBox"));
        saveSelectionCheckBox.setToolTipText(ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("saveCheckBoxToolTip"));
        panel3.add(saveSelectionCheckBox, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel3.add(separator1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        windowTitleLabel = new JLabel();
        this.$$$loadLabelText$$$(windowTitleLabel, ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("windowTitle"));
        panel3.add(windowTitleLabel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 2, false));
        openRadioButton = new JRadioButton();
        openRadioButton.setSelected(true);
        this.$$$loadButtonText$$$(openRadioButton, ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("selectionOpen"));
        panel4.add(openRadioButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editRadioButton = new JRadioButton();
        this.$$$loadButtonText$$$(editRadioButton, ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("selectionEdit"));
        panel4.add(editRadioButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyRadioButton = new JRadioButton();
        this.$$$loadButtonText$$$(copyRadioButton, ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("selectionCopy"));
        panel4.add(copyRadioButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateQrRadioButton = new JRadioButton();
        this.$$$loadButtonText$$$(generateQrRadioButton, ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("selectionQr"));
        panel4.add(generateQrRadioButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyQrRadioButton = new JRadioButton();
        this.$$$loadButtonText$$$(copyQrRadioButton, ResourceBundle.getBundle("translations/ModeSelectorDialogBundle").getString("selectionCopyQr"));
        panel4.add(copyQrRadioButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(openRadioButton);
        buttonGroup.add(openRadioButton);
        buttonGroup.add(copyQrRadioButton);
        buttonGroup.add(editRadioButton);
        buttonGroup.add(copyRadioButton);
        buttonGroup.add(generateQrRadioButton);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
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
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
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
        Translation translation = new Translation("ModeSelectorDialogBundle");
        windowTitleLabel.setText(translation.getTranslatedString("windowTitle"));
        openRadioButton.setText(translation.getTranslatedString("selectionOpen"));
        editRadioButton.setText(translation.getTranslatedString("selectionEdit"));
        copyRadioButton.setText(translation.getTranslatedString("selectionCopy"));
        generateQrRadioButton.setText(translation.getTranslatedString("selectionQr"));
        copyQrRadioButton.setText(translation.getTranslatedString("selectionCopyQr"));
        saveSelectionCheckBox.setText(translation.getTranslatedString("saveCheckBox"));
        saveSelectionCheckBox.setToolTipText(translation.getTranslatedString("saveCheckBoxToolTip"));


        Translation common = new Translation("CommonsBundle");
        buttonOK.setText(common.getTranslatedString("okButton"));
        buttonCancel.setText(common.getTranslatedString("cancelButton"));
    }
}

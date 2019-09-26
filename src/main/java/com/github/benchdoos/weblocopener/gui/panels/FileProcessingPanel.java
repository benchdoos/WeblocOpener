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

import com.github.benchdoos.linksupport.links.Link;
import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.core.constants.ArgumentConstants;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.gui.Translatable;
import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.service.links.LinkFactory;
import com.github.benchdoos.weblocopener.utils.system.OperatingSystem;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.log4j.Log4j2;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringJoiner;

@Log4j2
public class FileProcessingPanel extends JPanel implements SettingsPanel, Translatable {
    private JPanel contentPane;
    private String mode;

    private JPanel unixOpenModePanel;
    private JLabel unixOpenModeLabel;
    private JComboBox<UnixOpenMode> unixOpenModeComboBox;
    private JLabel convertToLabel;
    private JComboBox<String> converterComboBox;
    private JLabel createFileByDefaultLabel;
    private JComboBox<Link> createFileExtensionComboBox;
    private JCheckBox openInBrowser;

    public FileProcessingPanel() {
        initGui();
    }

    private void initGui() {
        setLayout(new GridLayout());
        add(contentPane);
        initUnixOpenModeComboBox();
        initUnixOpenModePanel();
        fillUnixOpenModeComboBox();
        loadUnixOpenModeComboBox();
        fillConvertComboBox();

        initCreateFileExtensionComboBox();
        fillCreateFileExtensionComboBox();

        translate();
    }

    private void initCreateFileExtensionComboBox() {
        createFileExtensionComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Link) {
                    final Link link = (Link) value;

                    return super.getListCellRendererComponent(list, link.getName(), index, isSelected, cellHasFocus);
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }

    private void fillCreateFileExtensionComboBox() {
        final List<Link> supportedLinks = LinkFactory.getSupportedLinks();
        final DefaultComboBoxModel<Link> model = new DefaultComboBoxModel<>();
        for (Link link : supportedLinks) {
            model.addElement(link);
        }
        createFileExtensionComboBox.setModel(model);
    }

    @Override
    public void translate() {
        Translation translation = new Translation("MainSetterPanelBundle");
        convertToLabel.setText(translation.getTranslatedString("convertTo"));
        unixOpenModeLabel.setText(translation.getTranslatedString("unixOpenModeLabel"));
        createFileByDefaultLabel.setText(translation.getTranslatedString("createFileLabel"));
        openInBrowser.setText(translation.getTranslatedString("openInFileBrowser"));
    }

    @Override
    public void loadSettings() {
        openInBrowser.setSelected(PreferencesManager.openFolderForNewFile());
        loadConverterValue();
        loadUnixOpenModeComboBox();
        loadCreateFile();
    }

    private void loadCreateFile() {
        final Link link = PreferencesManager.getLink();
        createFileExtensionComboBox.setSelectedItem(link);
    }

    private void fillConvertComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("*." + ApplicationConstants.URL_FILE_EXTENSION);
        model.addElement("*." + ApplicationConstants.DESKTOP_FILE_EXTENSION);
        converterComboBox.setModel(model);
    }

    @Override
    public String getName() {
        return Translation.getTranslatedString("SettingsDialogBundle", "fileProcessingPanelName");
    }

    @Override
    public void saveSettings() {
        final Object converterMode = converterComboBox.getSelectedItem();

        final UnixOpenMode mode = ((UnixOpenMode) unixOpenModeComboBox.getSelectedItem());
        if (mode != null) {
            this.mode = mode.getMode();
        }

        PreferencesManager.setOpenFolderForNewFile(openInBrowser.isSelected());

        saveCreateLinkValue();

        saveConverterValue();
        log.info("Saving settings: " +
                        "converter will save: {}, " +
                        "unix mode: {}",
                converterMode, this.mode);


        if (OperatingSystem.isUnix()) {
            PreferencesManager.setUnixOpeningMode(this.mode);
        }
    }

    private void saveCreateLinkValue() {
        final Link selectedItem = ((Link) createFileExtensionComboBox.getSelectedItem());
        PreferencesManager.setLink(selectedItem);
    }

    private void initUnixOpenModeComboBox() {
        unixOpenModeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof UnixOpenMode) {
                    final UnixOpenMode mode = (UnixOpenMode) value;
                    return super.getListCellRendererComponent(list, mode.getModeName(), index, isSelected, cellHasFocus);
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        unixOpenModeComboBox.addActionListener(e -> {
            final UnixOpenMode selectedItem = ((UnixOpenMode) unixOpenModeComboBox.getSelectedItem());
            mode = selectedItem.getMode();
        });
    }

    private void loadUnixOpenModeComboBox() {
        this.mode = PreferencesManager.getUnixOpeningMode();

        final ComboBoxModel<UnixOpenMode> model = unixOpenModeComboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            final UnixOpenMode mode = model.getElementAt(i);
            if (mode.getMode().equalsIgnoreCase(this.mode)) {
                unixOpenModeComboBox.setSelectedItem(mode);
            }
        }
    }

    private void fillUnixOpenModeComboBox() {
        UnixOpenMode defaultMode = new UnixOpenMode(SettingsConstants.OPENER_UNIX_DEFAULT_SELECTOR_MODE,
                Translation.getTranslatedString("MainSetterPanelBundle", "unixModeAsk"));
        UnixOpenMode openMode = new UnixOpenMode(ArgumentConstants.OPENER_OPEN_ARGUMENT,
                Translation.getTranslatedString("MainSetterPanelBundle", "unixModeOpen"));
        UnixOpenMode editMode = new UnixOpenMode(ArgumentConstants.OPENER_EDIT_ARGUMENT,
                Translation.getTranslatedString("MainSetterPanelBundle", "unixModeEdit"));
        UnixOpenMode copyMode = new UnixOpenMode(ArgumentConstants.OPENER_COPY_LINK_ARGUMENT,
                Translation.getTranslatedString("MainSetterPanelBundle", "unixModeCopy"));
        UnixOpenMode generateQrMode = new UnixOpenMode(ArgumentConstants.OPENER_QR_ARGUMENT,
                Translation.getTranslatedString("MainSetterPanelBundle", "unixModeGenerateQR"));
        UnixOpenMode copyQr = new UnixOpenMode(ArgumentConstants.OPENER_COPY_QR_ARGUMENT,
                Translation.getTranslatedString("MainSetterPanelBundle", "unixModeCopyQR"));


        DefaultComboBoxModel<UnixOpenMode> model = new DefaultComboBoxModel<>();
        model.addElement(defaultMode);
        model.addElement(openMode);
        model.addElement(editMode);
        model.addElement(copyMode);
        model.addElement(generateQrMode);
        model.addElement(copyQr);


        unixOpenModeComboBox.setModel(model);
    }

    private void initUnixOpenModePanel() {
        unixOpenModePanel.setVisible(OperatingSystem.isUnix());
    }

    private void saveConverterValue() {
        final Object selectedItem = converterComboBox.getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem.toString().toLowerCase().contains(ApplicationConstants.URL_FILE_EXTENSION)) {
                PreferencesManager.setConverterExportExtension(ApplicationConstants.URL_FILE_EXTENSION);
            } else if (selectedItem.toString().toLowerCase().contains(ApplicationConstants.DESKTOP_FILE_EXTENSION)) {
                PreferencesManager.setConverterExportExtension(ApplicationConstants.DESKTOP_FILE_EXTENSION);
            }
        }
    }

    private void loadConverterValue() {
        DefaultComboBoxModel<String> model = ((DefaultComboBoxModel<String>) converterComboBox.getModel());
        if (model != null) {
            for (int i = 0; i < model.getSize(); i++) {
                final String current = model.getElementAt(i);
                final String converterExportExtension = PreferencesManager.getConverterExportExtension();
                if (current.contains(converterExportExtension)) {
                    converterComboBox.setSelectedItem(current);
                }
            }
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
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        convertToLabel = new JLabel();
        this.$$$loadLabelText$$$(convertToLabel, ResourceBundle.getBundle("translations/MainSetterPanelBundle").getString("convertTo"));
        panel2.add(convertToLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        createFileByDefaultLabel = new JLabel();
        this.$$$loadLabelText$$$(createFileByDefaultLabel, ResourceBundle.getBundle("translations/MainSetterPanelBundle").getString("createFileLabel"));
        panel3.add(createFileByDefaultLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createFileExtensionComboBox = new JComboBox();
        panel3.add(createFileExtensionComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        unixOpenModePanel = new JPanel();
        unixOpenModePanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(unixOpenModePanel, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unixOpenModeLabel = new JLabel();
        this.$$$loadLabelText$$$(unixOpenModeLabel, ResourceBundle.getBundle("translations/MainSetterPanelBundle").getString("unixOpenModeLabel"));
        unixOpenModePanel.add(unixOpenModeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        unixOpenModeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Ask every time");
        defaultComboBoxModel1.addElement("Open");
        defaultComboBoxModel1.addElement("Edit");
        defaultComboBoxModel1.addElement("Copy to clipboard");
        defaultComboBoxModel1.addElement("Generate QR-Code");
        defaultComboBoxModel1.addElement("Copy QR-Code");
        unixOpenModeComboBox.setModel(defaultComboBoxModel1);
        unixOpenModePanel.add(unixOpenModeComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        unixOpenModePanel.add(separator1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        converterComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        converterComboBox.setModel(defaultComboBoxModel2);
        panel2.add(converterComboBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openInBrowser = new JCheckBox();
        this.$$$loadButtonText$$$(openInBrowser, ResourceBundle.getBundle("translations/MainSetterPanelBundle").getString("openInFileBrowser"));
        panel2.add(openInBrowser, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel2.add(separator2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        convertToLabel.setLabelFor(converterComboBox);
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

    class UnixOpenMode {
        private final String mode;
        private final String modeName;

        UnixOpenMode(String mode, String modeName) {
            this.mode = mode;

            this.modeName = modeName;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", UnixOpenMode.class.getSimpleName() + "[", "]")
                    .add("mode='" + mode + "'")
                    .add("modeName='" + modeName + "'")
                    .toString();
        }

        public String getMode() {
            return mode;
        }

        public String getModeName() {
            return modeName;
        }
    }
}

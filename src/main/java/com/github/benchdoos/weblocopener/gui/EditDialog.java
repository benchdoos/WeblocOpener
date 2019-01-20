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

package com.github.benchdoos.weblocopener.gui;

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.service.UrlsProceed;
import com.github.benchdoos.weblocopener.service.gui.ClickListener;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.FrameUtils;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.UserUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import static com.github.benchdoos.weblocopener.core.constants.StringConstants.FAVICON_GETTER_URL;


public class EditDialog extends JFrame {
    private final static int DEFAULT_APPLICATION_WIDTH = 450;
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    private final String pathToEditingFile;
    private String path = "";
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField;
    private String errorTitle = "Error";
    private JLabel createWeblocFileTextPane;
    private JLabel urlPageTitle;
    private JButton clearTextButton;

    @SuppressWarnings("unchecked")
    public EditDialog(String pathToEditingFile) {
        this.pathToEditingFile = pathToEditingFile;
        $$$setupUI$$$();
        translateDialog();

        initGui();

        log.debug("Got path: [" + pathToEditingFile + "]");
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
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
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 10, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 0, 10), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        this.$$$loadButtonText$$$(buttonOK, ResourceBundle.getBundle("translations/EditDialogBundle").getString("buttonOk"));
        buttonOK.putClientProperty("hideActionText", Boolean.FALSE);
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, ResourceBundle.getBundle("translations/EditDialogBundle").getString("buttonCancel"));
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        urlPageTitle = new JLabel();
        urlPageTitle.setForeground(new Color(-6316129));
        urlPageTitle.setHorizontalTextPosition(11);
        urlPageTitle.setText("");
        panel1.add(urlPageTitle, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(16, 16), null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(5, 10, 0, 10), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        Font textFieldFont = this.$$$getFont$$$(null, -1, 12, textField.getFont());
        if (textFieldFont != null) textField.setFont(textFieldFont);
        textField.setText("");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(textField, gbc);
        clearTextButton = new JButton();
        clearTextButton.setBorderPainted(false);
        clearTextButton.setContentAreaFilled(false);
        clearTextButton.setIcon(new ImageIcon(getClass().getResource("/images/closeButtons/circleGray12.png")));
        clearTextButton.setMargin(new Insets(6, 6, 6, 6));
        clearTextButton.setOpaque(false);
        clearTextButton.setPressedIcon(new ImageIcon(getClass().getResource("/images/closeButtons/circleRedDarker12.png")));
        clearTextButton.setRolloverIcon(new ImageIcon(getClass().getResource("/images/closeButtons/circleRed12.png")));
        clearTextButton.setText("");
        clearTextButton.setToolTipText(ResourceBundle.getBundle("translations/EditDialogBundle").getString("clearTextToolTip"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(clearTextButton, gbc);
        createWeblocFileTextPane = new JLabel();
        createWeblocFileTextPane.setAutoscrolls(true);
        createWeblocFileTextPane.setEnabled(true);
        createWeblocFileTextPane.setFocusable(false);
        Font createWeblocFileTextPaneFont = this.$$$getFont$$$(null, -1, 14, createWeblocFileTextPane.getFont());
        if (createWeblocFileTextPaneFont != null) createWeblocFileTextPane.setFont(createWeblocFileTextPaneFont);
        createWeblocFileTextPane.setOpaque(false);
        createWeblocFileTextPane.setRequestFocusEnabled(true);
        this.$$$loadLabelText$$$(createWeblocFileTextPane, ResourceBundle.getBundle("translations/EditDialogBundle").getString("EditWeblocLink"));
        createWeblocFileTextPane.setVerifyInputWhenFocusTarget(false);
        createWeblocFileTextPane.setVisible(true);
        panel3.add(createWeblocFileTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    private void createUIComponents() {
        textField = new PlaceholderTextField();
        ((PlaceholderTextField) textField).setPlaceholder("URL");
    }

    private void fillTextField(String pathToEditingFile) {
        try {
            log.debug("Filling textfield by file content: " + pathToEditingFile);
            URL url = new URL(UrlsProceed.takeUrl(new File(pathToEditingFile)));
            textField.setText(url.toString());
            textField.setCaretPosition(textField.getText().length());
            textField.selectAll();
            log.debug("Got URL [" + url + "] from [" + pathToEditingFile + "]");
        } catch (Exception e) {
            log.warn("Can not read url from: [" + pathToEditingFile + "]: ", e);
            fillTextFieldWithClipboard();
        }
    }

    private void fillTextFieldByUrl(File chosenFile) throws Exception {
        URL url = new URL(UrlsProceed.takeUrl(chosenFile));
        textField.setText(url.toString());
        textField.setCaretPosition(textField.getText().length());
        textField.selectAll();
        log.debug("Got URL [{}] from [{}]", url, chosenFile);
    }

    private void fillTextFieldWithClipboard() {
        String data = "<empty clipboard>";
        try {
            data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            URL url = new URL(data);
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(data)) {
                textField.setText(url.toString());
                setTextFieldFont(textField.getFont(), TextAttribute.UNDERLINE_ON);
                textField.setCaretPosition(textField.getText().length());
                textField.selectAll();
                log.debug("Got URL from clipboard: " + url);
            }
        } catch (UnsupportedFlavorException | IllegalStateException | HeadlessException | IOException e) {
            textField.setText("");
            log.warn("Can not read URL from clipboard: [" + data + "]", e);
        }
    }

    private void initGui() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/webloc256.png")));

        setContentPane(contentPane);


        getRootPane().setDefaultButton(buttonOK);

        clearTextButton.addActionListener(e -> onClearText());

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if (textField.getText().isEmpty()) {
                    fillTextFieldWithClipboard();
                }
                super.windowActivated(e);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        createWeblocFileTextPane.setBackground(new Color(232, 232, 232));


        initTextField(pathToEditingFile);

        pack();

        final Dimension dimension = new Dimension(DEFAULT_APPLICATION_WIDTH, getSize().height);
        setSize(dimension);
        setResizable(false);

        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
    }

    private void initTextField(String pathToEditingFile) {
        textField.addMouseListener(new ClickListener() {
            @Override
            public void doubleClick(MouseEvent e) {
                textField.selectAll();
            }
        });

        textField.getDocument().addDocumentListener(new DocumentListener() {
            Runnable runnable = () -> {
                updatePageTitle();
                updatePageFaviconIcon();
            };

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTextFont();
                new Thread(runnable).start();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTextFont();
                new Thread(runnable).start();
            }

            private void updatePageFaviconIcon() {
                final String pageUrl = textField.getText();
                log.debug("Getting favicons from url: {}");
                if (pageUrl != null && !pageUrl.isEmpty()) {
                    try {
                        URL url = new URL(FAVICON_GETTER_URL + pageUrl);
                        log.debug("Creating connection");

                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.setConnectTimeout(3000);

                        log.debug("Connection created successfully");

                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), "UTF-8"));

                        String input = bufferedReader.readLine();

                        JsonParser parser = new JsonParser();
                        JsonObject root = parser.parse(input).getAsJsonObject();
                        final JsonArray icons = root.getAsJsonArray("icons");
                        if (icons.size() > 0) {
                            final JsonElement jsonElement = icons.get(0);
                            final String faviconUrl = jsonElement.getAsJsonObject().getAsJsonPrimitive("url").getAsString();
                            final BufferedImage read = ImageIO.read(new URL(faviconUrl));
                            urlPageTitle.setIcon(new ImageIcon(CoreUtils.resize(read, 16, 16)));
                        }
                    } catch (Exception e) {
                        log.warn("Could not load favicon for page: {}", pageUrl, e);
                        urlPageTitle.setIcon(null);
                    }
                } else {
                    urlPageTitle.setIcon(null);
                }
            }

            private void updatePageTitle() {
                String url = textField.getText();
                if (url != null && !url.isEmpty()) {
                    try (InputStream response = new URL(url).openStream()) {
                        Scanner scanner = new Scanner(response);
                        String responseBody = scanner.useDelimiter("\\A").next();

                        String fullTitle = responseBody.substring(responseBody.indexOf("<title>") + 7, responseBody.indexOf("</title>"));
                        urlPageTitle.setToolTipText(fullTitle);

                        if (fullTitle.length() > 40) {
                            urlPageTitle.setText(fullTitle.substring(0, 40) + "...");
                        } else {
                            urlPageTitle.setText(fullTitle);
                        }
                    } catch (IOException ex) {
                        log.warn("Could not get page title: {}", ex.getMessage());
                        urlPageTitle.setText(null);
                    }
                } else {
                    log.debug("Page is null, so title is clear");
                    urlPageTitle.setText(null);
                }
            }

            private void updateTextFont() {
                UrlValidator urlValidator = new UrlValidator();
                if (urlValidator.isValid(textField.getText())) {
                    if (textField != null) {
                        setTextFieldFont(textField.getFont(), TextAttribute.UNDERLINE_ON);
                        textField.setForeground(Color.BLUE);
                    }
                } else {
                    if (textField != null) {
                        setTextFieldFont(textField.getFont(), TextAttribute.SUPERSCRIPT_SUB);
                        textField.setForeground(Color.BLACK);
                    }
                }
            }

        });

        UndoManager undoManager = new UndoManager();
        textField.getDocument().addUndoableEditListener(evt -> undoManager.addEdit(evt.getEdit()));

        textField.getActionMap().put("Undo",
                new AbstractAction("Undo") {
                    public void actionPerformed(ActionEvent evt) {
                        try {
                            if (undoManager.canUndo()) {
                                undoManager.undo();
                            }
                        } catch (CannotUndoException ignore) {
                            /*NOP*/
                        }
                    }
                });

        textField.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

        textField.getActionMap().put("Redo",
                new AbstractAction("Redo") {
                    public void actionPerformed(ActionEvent evt) {
                        try {
                            if (undoManager.canRedo()) {
                                undoManager.redo();
                            }
                        } catch (CannotRedoException ignore) {
                            /*NOP*/
                        }
                    }
                });

        textField.getInputMap().put(KeyStroke.getKeyStroke("control shift Z"), "Redo");

        fillTextField(pathToEditingFile);
    }

    private void onCancel() {
        dispose();
    }

    private void onClearText() {
        log.debug("Clearing textfield");
        textField.setText(null);
        urlPageTitle.setIcon(null);
        urlPageTitle.setText(null);
    }

    private void onOK() {
        try {
            URL url = new URL(textField.getText());
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(textField.getText())) {
                UrlsProceed.createWebloc(pathToEditingFile, url);
                dispose();
            } else {
                throw new MalformedURLException();
            }
        } catch (MalformedURLException e) {
            log.warn("Can not parse URL: [" + textField.getText() + "]", e);


            final String[] message = {""};
            Translation translation = new Translation("translations/EditDialogBundle") {
                @Override
                public void initTranslations() {
                    message[0] = messages.getString("incorrectUrlMessage") + ": [";
                }
            };
            translation.initTranslations();

            String incorrectUrl = textField.getText()
                    .substring(0, Math.min(textField.getText().length(), 50));
            //Fixes EditDialog long url message showing issue
            message[0] += textField.getText().length() > incorrectUrl.length() ? incorrectUrl + "...]" : incorrectUrl + "]";


            UserUtils.showWarningMessageToUser(this, errorTitle,
                    message[0]);
        }

    }

    private void setTextFieldFont(Font font, int attribute2) {
        Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.UNDERLINE, attribute2);
        textField.setFont(font.deriveFont(fontAttributes));
    }

    @Override
    public void setVisible(boolean b) {
        FrameUtils.showOnTop(this);
        super.setVisible(b);
    }

    private void translateDialog() {
        Translation translation = new Translation("translations/EditDialogBundle") {
            @Override
            public void initTranslations() {
                String path = "";
                try {
                    path = new File(pathToEditingFile).getName();
                } catch (Exception e) {
                    log.warn("Could not get file name for: " + pathToEditingFile, e);
                }
                setTitle(path + " — WeblocOpener");
                ((PlaceholderTextField) textField).setPlaceholder(messages.getString("textField"));
                buttonOK.setText(messages.getString("buttonOk"));
                buttonCancel.setText(messages.getString("buttonCancel"));
                errorTitle = messages.getString("errorTitle");
            }
        };
        translation.initTranslations();
    }
}

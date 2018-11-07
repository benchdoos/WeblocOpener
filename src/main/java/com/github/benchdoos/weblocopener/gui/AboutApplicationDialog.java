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

package com.github.benchdoos.weblocopener.gui;

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.StringConstants;
import com.github.benchdoos.weblocopener.service.UrlsProceed;
import com.github.benchdoos.weblocopener.service.gui.MousePickListener;
import com.github.benchdoos.weblocopener.utils.CoreUtils;
import com.github.benchdoos.weblocopener.utils.FrameUtils;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.UserUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.BalloonTipPositioner;
import net.java.balloontip.positioners.LeftAbovePositioner;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.MinimalBalloonStyle;
import net.java.balloontip.utils.TimingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;


public class AboutApplicationDialog extends JDialog {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());
    private String librariesLabelToolTip = "Third-party Libraries used in WeblocOpener project.";
    private JPanel contentPane;
    private JTextPane descriptionTextPane;
    private JLabel versionLabel;
    private ImagePanel imagePanel1;
    private JScrollPane scrollPane;
    private JLabel siteLinkLabel;
    private JLabel githubLinkLabel;
    private JLabel logLabel;
    private JLabel feedbackLabel;
    private JLabel librariesLabel;
    private JLabel telegramLabel;
    private JLabel shareLabel;
    private JLabel donateLabel;
    private String shareLabelText;
    private String shareBalloonMessage;


    public AboutApplicationDialog() {
        $$$setupUI$$$();
        addWindowMoveListeners();
        translateDialog();
        initGui();

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
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.setOpaque(true);
        imagePanel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        imagePanel1.setAlignmentX(0.5f);
        imagePanel1.setAlignmentY(0.5f);
        imagePanel1.setAutoscrolls(true);
        imagePanel1.setOpaque(true);
        contentPane.add(imagePanel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-9923881));
        panel1.setOpaque(false);
        imagePanel1.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 9, new Insets(0, 10, 10, 10), -1, -1));
        panel2.setBackground(new Color(-9923881));
        panel2.setOpaque(false);
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        siteLinkLabel = new JLabel();
        this.$$$loadLabelText$$$(siteLinkLabel, ResourceBundle.getBundle("spelling").getString("site"));
        panel2.add(siteLinkLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        githubLinkLabel = new JLabel();
        this.$$$loadLabelText$$$(githubLinkLabel, ResourceBundle.getBundle("spelling").getString("github"));
        panel2.add(githubLinkLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logLabel = new JLabel();
        this.$$$loadLabelText$$$(logLabel, ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("logLabelTooltip"));
        panel2.add(logLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        feedbackLabel = new JLabel();
        feedbackLabel.setIcon(new ImageIcon(getClass().getResource("/images/feedbackIcon.png")));
        feedbackLabel.setText("");
        panel2.add(feedbackLabel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        librariesLabel = new JLabel();
        this.$$$loadLabelText$$$(librariesLabel, ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("librariesLabel"));
        panel2.add(librariesLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        telegramLabel = new JLabel();
        telegramLabel.setIcon(new ImageIcon(getClass().getResource("/images/telegramIcon16.png")));
        telegramLabel.setText("");
        panel2.add(telegramLabel, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shareLabel = new JLabel();
        shareLabel.setIcon(new ImageIcon(getClass().getResource("/images/shareIcon16.png")));
        shareLabel.setText("");
        panel2.add(shareLabel, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        donateLabel = new JLabel();
        donateLabel.setIcon(new ImageIcon(getClass().getResource("/images/donate16.png")));
        donateLabel.setText("");
        panel2.add(donateLabel, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 0, 10), -1, -1));
        panel3.setOpaque(false);
        panel1.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setAlignmentX(0.0f);
        panel4.setAlignmentY(0.0f);
        panel4.setBackground(new Color(-9923881));
        panel4.setOpaque(false);
        panel4.setRequestFocusEnabled(true);
        panel4.setVisible(true);
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane.setBackground(new Color(-9923881));
        scrollPane.setFocusable(false);
        scrollPane.setOpaque(false);
        scrollPane.setVisible(true);
        scrollPane.setWheelScrollingEnabled(true);
        panel4.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(400, 160), null, 0, false));
        descriptionTextPane.setBackground(new Color(-9923881));
        descriptionTextPane.setCaretColor(new Color(-1118482));
        descriptionTextPane.setContentType(ResourceBundle.getBundle("spelling").getString("text.html"));
        descriptionTextPane.setDragEnabled(false);
        descriptionTextPane.setEditable(false);
        descriptionTextPane.setEnabled(true);
        descriptionTextPane.setFocusCycleRoot(false);
        descriptionTextPane.setFocusable(false);
        descriptionTextPane.setOpaque(false);
        descriptionTextPane.setRequestFocusEnabled(false);
        descriptionTextPane.setText(ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("aboutAppInfo"));
        descriptionTextPane.setVerifyInputWhenFocusTarget(false);
        descriptionTextPane.setVisible(true);
        scrollPane.setViewportView(descriptionTextPane);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setAlignmentX(0.0f);
        panel5.setAlignmentY(0.0f);
        panel5.setBackground(new Color(-9923881));
        panel5.setOpaque(false);
        panel3.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 24, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("spelling").getString("WeblocOpener"));
        panel5.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        versionLabel = new JLabel();
        Font versionLabelFont = this.$$$getFont$$$(null, -1, 11, versionLabel.getFont());
        if (versionLabelFont != null) versionLabel.setFont(versionLabelFont);
        versionLabel.setForeground(new Color(-3153931));
        this.$$$loadLabelText$$$(versionLabel, ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("appVersionLabel"));
        panel5.add(versionLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }


    private void addWindowMoveListeners() {
        MousePickListener mousePickListener = new MousePickListener(this);

        imagePanel1.addMouseListener(mousePickListener.getMouseAdapter);
        imagePanel1.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);

//        risingBalloonLogoLabel.addMouseListener(mousePickListener.getMouseAdapter);
//        risingBalloonLogoLabel.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);

        descriptionTextPane.addMouseListener(mousePickListener.getMouseAdapter);

        descriptionTextPane.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);
    }

    private void createUIComponents() {
        ImageIcon image = new ImageIcon(getClass().getResource("/images/background.png"));
        imagePanel1 = new ImagePanel(image);
        descriptionTextPane = new JTextPane();

        scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

    }

    private void initGui() {
        log.debug("Creating GUI");
        setContentPane(contentPane);
        setIconImage(Toolkit.getDefaultToolkit().getImage(AboutApplicationDialog.class.getResource("/images/balloonIcon256.png")));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        initLinks();

        setModal(true);
        setSize(550, 300);
        setResizable(false);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        log.debug("GUI created");
    }

    private void initLinks() {
        siteLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        siteLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UserUtils.openWebUrl(StringConstants.UPDATE_WEB_URL);
            }
        });
        siteLinkLabel.setToolTipText(StringConstants.UPDATE_WEB_URL);

        githubLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UserUtils.openWebUrl(StringConstants.GITHUB_WEB_URL);
            }
        });
        githubLinkLabel.setToolTipText(StringConstants.GITHUB_WEB_URL);

        librariesLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        librariesLabel.addMouseListener(new MouseAdapter() {
            private void createInfoDialog() {
                InfoDialog infoDialog = new InfoDialog();
                infoDialog.setTitle(librariesLabelToolTip);
                StringBuilder contentBuilder = new StringBuilder();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream("/pages/libs.html")))) {
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        contentBuilder.append(str);
                    }
                    infoDialog.setContent(contentBuilder.toString());
                    infoDialog.setVisible(true);
                } catch (IOException ignore) {/*NOP*/}
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                createInfoDialog();
            }
        });

        logLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().open(Logging.LOG_FOLDER);
                } catch (IOException e1) {
                    log.warn("Can not open log folder: " + Logging.LOG_FOLDER);
                }
            }
        });

        feedbackLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        feedbackLabel.addMouseListener(new MouseAdapter() {
            private void callMail() {
                try {
                    Desktop.getDesktop().mail(new URI(StringConstants.FEEDBACK_MAIL_URL));
                } catch (URISyntaxException | IOException ex) {
                    log.warn("Can not open mail for: '" + StringConstants.FEEDBACK_MAIL_URL + "'", ex);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                feedbackLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/images/feedbackIconPressed.png"))));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                feedbackLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/images/feedbackIcon.png"))));

                callMail();
            }
        });

        telegramLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        telegramLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UrlsProceed.openUrl(StringConstants.BENCH_DOOS_TELEGRAM_URL);
            }
        });

        donateLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        donateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UrlsProceed.openUrl(StringConstants.DONATE_PAYPAL_URL);
            }
        });


        shareLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        shareLabel.addMouseListener(new MouseAdapter() {
            private void createBalloonTip() {
                BalloonTip balloonTip = new BalloonTip(shareLabel, shareBalloonMessage);
                balloonTip.setCloseButton(null);
                BalloonTipStyle minimalStyle = new MinimalBalloonStyle(Color.WHITE, 5);
                balloonTip.setStyle(minimalStyle);
                BalloonTipPositioner balloonTipPositioner = new LeftAbovePositioner(0, 0);
                balloonTip.setPositioner(balloonTipPositioner);

                TimingUtils.showTimedBalloon(balloonTip, 4_000);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                shareLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/images/shareIconPressed16.png"))));

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                shareLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/images/shareIcon16.png"))));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(shareLabelText);
                clipboard.setContents(stringSelection, null);

                createBalloonTip();
            }
        });
    }

    private void translateDialog() {
        Translation translation = new Translation("translations/AboutApplicationDialogBundle") {
            @Override
            public void initTranslations() {
                setTitle(messages.getString("windowTitle"));
                versionLabel.setText(messages.getString("appVersionLabel") + " " + CoreUtils.getApplicationVersionString());

                siteLinkLabel.setText(createHtmlLink(messages.getString("visitLabel")));


                githubLinkLabel.setText(createHtmlLink("Github"));


                librariesLabel.setText(createHtmlLink(messages.getString("librariesLabel")));
                librariesLabelToolTip = messages.getString("librariesLabelToolTip");
                librariesLabel.setToolTipText(librariesLabelToolTip);


                logLabel.setText(createHtmlLink(messages.getString("logLabel")));


                logLabel.setToolTipText(messages.getString("logLabelTooltip"));

                feedbackLabel.setToolTipText(messages.getString("feedbackLabel"));


                telegramLabel.setToolTipText(messages.getString("telegramLabel"));

                shareBalloonMessage = messages.getString("shareBalloonMessage");

                shareLabel.setToolTipText(messages.getString("shareLabel"));

                shareLabelText = messages.getString("shareLabelText");

            }

            private String createHtmlLink(String string) {
                return "<html><a href=\"\">" + string + "</a></html>";
            }
        };
        translation.initTranslations();
    }

}
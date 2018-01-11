package com.github.benchdoos.weblocopener.weblocOpener.gui;

import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.core.Translation;
import com.github.benchdoos.weblocopener.commons.utils.FrameUtils;
import com.github.benchdoos.weblocopener.commons.utils.Logging;
import com.github.benchdoos.weblocopener.commons.utils.UserUtils;
import com.github.benchdoos.weblocopener.weblocOpener.service.UrlsProceed;
import com.github.benchdoos.weblocopener.weblocOpener.service.gui.MousePickListener;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.BalloonTipPositioner;
import net.java.balloontip.positioners.LeftAbovePositioner;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.MinimalBalloonStyle;
import net.java.balloontip.utils.TimingUtils;
import org.apache.log4j.Logger;

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

import static com.github.benchdoos.weblocopener.commons.utils.Logging.getCurrentClassName;


public class AboutApplicationDialog extends JDialog {
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    String librariesLabelToolTip = "Third-party Libraries used in WeblocOpener project.";
    private JPanel contentPane;
    private JTextPane weblocOpenerBWillTextPane;
    private JLabel versionLabel;
    private ImagePanel imagePanel1;
    private JScrollPane scrollPane1;
    private JLabel siteLinkLabel;
    private JLabel githubLinkLabel;
    private JLabel logLabel;
    private JLabel feedbackLabel;
    private JLabel librariesLabel;
    private JLabel telegramLabel;
    private JLabel shareLabel;
    private JLabel risingBalloonLogoLabel;
    private String shareLabelText;
    private String shareBalloonMessage;


    public AboutApplicationDialog() {
        $$$setupUI$$$();
        addWindowMoveListeners();
        translateDialog();

        initGui();

    }

    private void addEasterListener() {
        risingBalloonLogoLabel.addMouseListener(new MouseAdapter() {
            int clickCount = 0;
            int leftClickCount = 0;
            boolean easterShown = false;
            Timer timer = new Timer(500, e -> {
                if (clickCount < 3) {
                    clickCount = 0;
                } else {
                    showEaster();
                }

                if (leftClickCount < 2) {
                    leftClickCount = 0;
                }
            });

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (!timer.isRunning()) {
                        timer.setRepeats(true);
                        timer.start();
                    }
                    clickCount++;
                }

                if (e.getButton() == MouseEvent.BUTTON1 && easterShown) {
                    leftClickCount++;
                    if (leftClickCount >= 2) {
                        leftClickCount = 0;
                        UrlsProceed.openUrl("https://vk.cc/79FQIY"); //hardcoded not to give to find it in source code :<
                        dispose();
                    }
                }
            }

            private void showEaster() {
                risingBalloonLogoLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(ShowQrDialog.class.getResource("/easter.bin"))));
                easterShown = true;
            }
        });
    }

    private void addWindowMoveListeners() {
        MousePickListener mousePickListener = new MousePickListener(this);

        imagePanel1.addMouseListener(mousePickListener.getMouseAdapter);
        imagePanel1.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);

        risingBalloonLogoLabel.addMouseListener(mousePickListener.getMouseAdapter);
        risingBalloonLogoLabel.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);

        weblocOpenerBWillTextPane.addMouseListener(mousePickListener.getMouseAdapter);

        weblocOpenerBWillTextPane.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);
    }

    private void createUIComponents() {
        ImageIcon image = new ImageIcon(getClass().getResource("/about/background.png"));
        imagePanel1 = new ImagePanel(image);
        weblocOpenerBWillTextPane = new JTextPane();

        scrollPane1 = new JScrollPane();
        scrollPane1.setOpaque(false);
        scrollPane1.getViewport().setOpaque(false);
        scrollPane1.setBorder(BorderFactory.createEmptyBorder());

    }

    private void initGui() {
        log.debug("Creating GUI");
        setContentPane(contentPane);
        setIconImage(Toolkit.getDefaultToolkit().getImage(AboutApplicationDialog.class.getResource("/balloonIcon64.png")));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getRootPane().registerKeyboardAction(e -> {
            dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        initLinks();

        setModal(true);
        setSize(550, 300);
        setResizable(false);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        addEasterListener();
        log.debug("GUI created");
    }

    private void initLinks() {
        siteLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        siteLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UserUtils.openWebUrl(ApplicationConstants.UPDATE_WEB_URL);
            }
        });
        siteLinkLabel.setToolTipText(ApplicationConstants.UPDATE_WEB_URL);

        githubLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UserUtils.openWebUrl(ApplicationConstants.GITHUB_WEB_URL);
            }
        });
        githubLinkLabel.setToolTipText(ApplicationConstants.GITHUB_WEB_URL);

        librariesLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        librariesLabel.addMouseListener(new MouseAdapter() {
            private void createInfoDialog() {
                InfoDialog infoDialog = new InfoDialog();
                infoDialog.setTitle(librariesLabelToolTip);
                StringBuilder contentBuilder = new StringBuilder();
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(
                            getClass().getResourceAsStream("/pages/libs.html")));
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        contentBuilder.append(str);
                    }
                    infoDialog.content = contentBuilder.toString();
                    infoDialog.setVisible(true);
                } catch (IOException ignore) {/*NOP*/} finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException ignore) {/*NOP*/}
                    }
                }
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
                final String url = "mailto:weblocopener@gmail.com?subject=WeblocOpener%20feedback" +
                        "&body=Type%20here%20your%20question%20/%20problem,%20I%20try%20to%20help%20you%20as%20soon%20as%20it%20is%20possible!" +
                        "%0AYou%20can%20attach%20log%20files%20(see%20WeblocOpener%20-%20Settings%20-%20About%20-%20Log%20folder%20-%20zip%20log%20folder%20-%20attach)." +
                        "%0ADon't%20forget%20to%20close%20the%20application%20before%20zipping%20logs;)";
                try {
                    Desktop.getDesktop().mail(new URI(url));
                } catch (URISyntaxException | IOException ex) {
                    log.warn("Can not open mail for: '" + url + "'", ex);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                feedbackLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/feedbackIconPressed.png"))));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                feedbackLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/feedbackIcon.png"))));

                callMail();
            }
        });

        telegramLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        telegramLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UrlsProceed.openUrl(ApplicationConstants.BENCH_DOOS_TELEGRAM_URL);
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
                        .getImage(getClass().getResource("/shareIconPressed.png"))));

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                shareLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(getClass().getResource("/shareIcon.png"))));
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
                versionLabel.setText(messages.getString("appVersionLabel") + " " + ApplicationConstants.APP_VERSION);

                siteLinkLabel.setText("<html><a href=\"\">" + messages.getString("visitLabel") + "</a></html>");


                githubLinkLabel.setText("<html><a href=\"\">" + "Github" + "</a></html>");


                librariesLabel.setText("<html><a href=\"\">" + messages.getString("librariesLabel") + "</a></html>");
                librariesLabelToolTip = messages.getString("librariesLabelToolTip");
                librariesLabel.setToolTipText(librariesLabelToolTip);


                logLabel.setText("<html><a href=\"\">" + messages.getString("logLabel") + "</a></html>");


                logLabel.setToolTipText(messages.getString("logLabelTooltip"));

                feedbackLabel.setToolTipText(messages.getString("feedbackLabel"));


                telegramLabel.setToolTipText(messages.getString("telegramLabel"));

                shareBalloonMessage = messages.getString("shareBalloonMessage");

                shareLabel.setToolTipText(messages.getString("shareLabel"));

                shareLabelText = messages.getString("shareLabelText");

            }
        };
        translation.initTranslations();
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
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(10, 10, 40, 10), -1, -1));
        panel1.setBackground(new Color(-9923881));
        panel1.setOpaque(false);
        imagePanel1.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setAlignmentX(0.0f);
        panel2.setAlignmentY(0.0f);
        panel2.setBackground(new Color(-9923881));
        panel2.setOpaque(false);
        panel2.setRequestFocusEnabled(true);
        panel2.setVisible(true);
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane1.setBackground(new Color(-9923881));
        scrollPane1.setFocusable(false);
        scrollPane1.setOpaque(false);
        scrollPane1.setVisible(true);
        scrollPane1.setWheelScrollingEnabled(true);
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(400, 160), null, 0, false));
        weblocOpenerBWillTextPane.setBackground(new Color(-9923881));
        weblocOpenerBWillTextPane.setCaretColor(new Color(-1118482));
        weblocOpenerBWillTextPane.setContentType("text/html");
        weblocOpenerBWillTextPane.setDragEnabled(false);
        weblocOpenerBWillTextPane.setEditable(false);
        weblocOpenerBWillTextPane.setEnabled(true);
        weblocOpenerBWillTextPane.setFocusCycleRoot(false);
        weblocOpenerBWillTextPane.setFocusable(false);
        weblocOpenerBWillTextPane.setOpaque(false);
        weblocOpenerBWillTextPane.setRequestFocusEnabled(false);
        weblocOpenerBWillTextPane.setText(ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("aboutAppInfo"));
        weblocOpenerBWillTextPane.setVerifyInputWhenFocusTarget(false);
        weblocOpenerBWillTextPane.setVisible(true);
        scrollPane1.setViewportView(weblocOpenerBWillTextPane);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setAlignmentX(0.0f);
        panel3.setAlignmentY(0.0f);
        panel3.setBackground(new Color(-9923881));
        panel3.setOpaque(false);
        panel1.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 24, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setForeground(new Color(-1));
        label1.setText("WeblocOpener");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        versionLabel = new JLabel();
        Font versionLabelFont = this.$$$getFont$$$(null, -1, 10, versionLabel.getFont());
        if (versionLabelFont != null) versionLabel.setFont(versionLabelFont);
        versionLabel.setForeground(new Color(-3153931));
        this.$$$loadLabelText$$$(versionLabel, ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("appVersionLabel"));
        panel3.add(versionLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setOpaque(false);
        panel1.add(panel4, new GridConstraints(0, 1, 3, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        risingBalloonLogoLabel = new JLabel();
        risingBalloonLogoLabel.setIcon(new ImageIcon(getClass().getResource("/about/balloon_animated.gif")));
        risingBalloonLogoLabel.setText("");
        panel4.add(risingBalloonLogoLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 8, new Insets(0, 0, 0, 10), -1, -1));
        panel5.setBackground(new Color(-9923881));
        panel5.setOpaque(false);
        panel1.add(panel5, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel5.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        siteLinkLabel = new JLabel();
        siteLinkLabel.setText("site");
        panel5.add(siteLinkLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        githubLinkLabel = new JLabel();
        githubLinkLabel.setText("github");
        panel5.add(githubLinkLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logLabel = new JLabel();
        this.$$$loadLabelText$$$(logLabel, ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("logLabelTooltip"));
        panel5.add(logLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        feedbackLabel = new JLabel();
        feedbackLabel.setIcon(new ImageIcon(getClass().getResource("/feedbackIcon.png")));
        feedbackLabel.setText("");
        panel5.add(feedbackLabel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        librariesLabel = new JLabel();
        this.$$$loadLabelText$$$(librariesLabel, ResourceBundle.getBundle("translations/AboutApplicationDialogBundle").getString("librariesLabel"));
        panel5.add(librariesLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        telegramLabel = new JLabel();
        telegramLabel.setIcon(new ImageIcon(getClass().getResource("/telegramIcon16.png")));
        telegramLabel.setText("");
        panel5.add(telegramLabel, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shareLabel = new JLabel();
        shareLabel.setIcon(new ImageIcon(getClass().getResource("/shareIcon.png")));
        shareLabel.setText("");
        panel5.add(shareLabel, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
}

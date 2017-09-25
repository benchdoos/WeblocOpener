package com.doos.webloc_opener.gui;

import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.core.Translation;
import com.doos.commons.utils.FrameUtils;
import com.doos.commons.utils.Logging;
import com.doos.commons.utils.UserUtils;
import com.doos.webloc_opener.service.UrlsProceed;
import com.doos.webloc_opener.service.gui.MousePickListener;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.BalloonTipPositioner;
import net.java.balloontip.positioners.LeftAbovePositioner;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.MinimalBalloonStyle;
import net.java.balloontip.utils.TimingUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
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

import static com.doos.commons.utils.Logging.getCurrentClassName;


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
    private String shareLabelText;
    private String shareBalloonMessage;


    public AboutApplicationDialog() {
        translateDialog();

        initGui();
    }

    private void addWindowMoveListeners() {
        MousePickListener mousePickListener = new MousePickListener(this);

        imagePanel1.addMouseListener(mousePickListener.getMouseAdapter);
        imagePanel1.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);

        weblocOpenerBWillTextPane.addMouseListener(mousePickListener.getMouseAdapter);

        weblocOpenerBWillTextPane.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);
    }

    private void createUIComponents() {
        try {
            imagePanel1 = new ImagePanel(ImageIO.read(ImagePanel.class.getResource("/about/background.png")));
            weblocOpenerBWillTextPane = new JTextPane();
            addWindowMoveListeners();
        } catch (IOException e) {
            log.warn("Can not read background for AboutApplicationDialog", e);
        }

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
                            getClass().getResourceAsStream("/resources/pages/libs.html")));
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
                        .getImage(SettingsDialog.class.getResource("/feedbackIconPressed.png"))));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                feedbackLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(SettingsDialog.class.getResource("/feedbackIcon.png"))));

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
                        .getImage(SettingsDialog.class.getResource("/shareIconPressed.png"))));

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                shareLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
                        .getImage(SettingsDialog.class.getResource("/shareIcon.png"))));
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
}

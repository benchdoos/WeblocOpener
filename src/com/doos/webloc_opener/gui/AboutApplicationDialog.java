package com.doos.webloc_opener.gui;

import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.core.Translation;
import com.doos.commons.utils.FrameUtils;
import com.doos.commons.utils.Logging;
import com.doos.commons.utils.UserUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.doos.commons.utils.Logging.getCurrentClassName;


public class AboutApplicationDialog extends JDialog {
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    private JPanel contentPane;
    private JTextPane weblocOpenerBWillTextPane;
    private JLabel versionLabel;
    private JLabel visitSiteLabel;
    private ImagePanel imagePanel1;
    private JScrollPane scrollPane1;
    private JLabel visitGithubLabel;
    private JLabel siteLinkLabel;
    private JLabel githubLinkLabel;
    private JLabel logFolderLabel;
    private JLabel feedbackLabel;

    public AboutApplicationDialog() {

        setContentPane(contentPane);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/balloonIcon64.png")));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getRootPane().registerKeyboardAction(e -> {
            dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setModal(true);
        setSize(550, 300);
        setResizable(false);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));

        translateDialog();
    }

    private void translateDialog() {
        Translation translation = new Translation("translations/AboutApplicationDialogBundle") {
            @Override
            public void initTranslations() {
                setTitle(messages.getString("windowTitle"));
                versionLabel.setText(messages.getString("appVersionLabel") + " " + ApplicationConstants.APP_VERSION);
                visitSiteLabel.setText(messages.getString("visitLabel") + ":");

                siteLinkLabel.setText("<html><a href=\"\">" + ApplicationConstants.UPDATE_WEB_URL + "</a></html>");
                siteLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                siteLinkLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        UserUtils.openWebUrl(ApplicationConstants.UPDATE_WEB_URL);
                    }
                });
                siteLinkLabel.setToolTipText(ApplicationConstants.UPDATE_WEB_URL);

                visitGithubLabel.setText(messages.getString("visitGithubLabel") + ":");

                githubLinkLabel.setText("<html><a href=\"\">" + "Github" + "</a></html>");
                githubLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                githubLinkLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        UserUtils.openWebUrl(ApplicationConstants.GITHUB_WEB_URL);
                    }
                });
                githubLinkLabel.setToolTipText(ApplicationConstants.GITHUB_WEB_URL);

                logFolderLabel.setText("<html><a href=\"\">" + messages.getString("logFolderLabel") + "</a></html>");
                logFolderLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                logFolderLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop.getDesktop().open(Logging.LOG_FOLDER);
                        } catch (IOException e1) {
                            log.warn("Can not open log folder: " + Logging.LOG_FOLDER);
                        }
                    }
                });
                logFolderLabel.setToolTipText(messages.getString("logFolderLabel"));

                feedbackLabel.setText("<html><a href=\"\">" + "feedback" + "</a></html>");
                feedbackLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                feedbackLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
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
                });
            }
        };
        translation.initTranslations();
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

    private void addWindowMoveListeners() {
        Window parent = this;
        final Point[] initialClick = new Point[1];

        final MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint();
                getComponentAt(initialClick[0]);
            }
        };

        final MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                // get location of Window
                int thisX = parent.getLocation().x;
                int thisY = parent.getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = (thisX + e.getX()) - (thisX + initialClick[0].x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick[0].y);

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                parent.setLocation(X, Y);
            }
        };

        imagePanel1.addMouseListener(mouseAdapter);
        imagePanel1.addMouseMotionListener(mouseMotionAdapter);

        weblocOpenerBWillTextPane.addMouseListener(mouseAdapter);

        weblocOpenerBWillTextPane.addMouseMotionListener(mouseMotionAdapter);
    }
}

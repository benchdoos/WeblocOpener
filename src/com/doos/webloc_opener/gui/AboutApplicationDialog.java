package com.doos.webloc_opener.gui;

import com.doos.commons.ApplicationConstants;
import com.doos.commons.Translation;
import com.doos.commons.utils.FrameUtils;
import com.doos.commons.utils.UserUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

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

    public AboutApplicationDialog() {

        setContentPane(contentPane);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/balloonIcon64.png")));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
                        UserUtils.openAppGithubPage(ApplicationConstants.UPDATE_WEB_URL);
                    }
                });
                siteLinkLabel.setToolTipText(ApplicationConstants.UPDATE_WEB_URL);

                visitGithubLabel.setText(messages.getString("visitGithubLabel") + ":");

                githubLinkLabel.setText("<html><a href=\"\">" + "Github" + "</a></html>");
                githubLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                githubLinkLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        UserUtils.openAppGithubPage(ApplicationConstants.GITHUB_WEB_URL);
                    }
                });
                githubLinkLabel.setToolTipText(ApplicationConstants.GITHUB_WEB_URL);
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

package com.doos.webloc_opener.gui;

import com.doos.commons.ApplicationConstants;
import com.doos.commons.Translation;
import com.doos.commons.utils.FrameUtils;
import com.doos.commons.utils.UserUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;


public class AboutApplicationDialog extends JDialog {
    private JPanel contentPane;
    private JTextPane weblocOpenerBWillTextPane;
    private JLabel versionLabel;
    private JLabel visitSiteLabel;
    private ImagePanel imagePanel1;
    private JScrollPane scrollPane1;

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
                visitSiteLabel.setText("<html> " + messages.getString("visitLabel") + ": " +
                                               "<a href=\"\">" + ApplicationConstants.UPDATE_WEB_URL + "</a></html>");
                visitSiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                visitSiteLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        UserUtils.openAppGithubPage();
                    }
                });
            }
        };
        translation.initTranslations();
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
        try {
            imagePanel1 = new ImagePanel(ImageIO.read(ImagePanel.class.getResource("/about/background.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollPane1 = new JScrollPane();
        scrollPane1.setOpaque(false);
        scrollPane1.getViewport().setOpaque(false);
        scrollPane1.setBorder(BorderFactory.createEmptyBorder());

    }
}

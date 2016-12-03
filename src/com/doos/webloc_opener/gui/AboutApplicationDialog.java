package com.doos.webloc_opener.gui;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.Translation;
import com.doos.settings_manager.utils.FrameUtils;
import com.doos.settings_manager.utils.UserUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class AboutApplicationDialog extends JDialog {
    private JPanel contentPane;
    private JTextPane weblocOpenerBWillTextPane;
    private JLabel versionLabel;
    private JLabel visitSiteLabel;

    public AboutApplicationDialog() {
        setContentPane(contentPane);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/balloonIcon64.png")));

        Color color = UIManager.getColor("Panel.background");

        weblocOpenerBWillTextPane.setBackground(color);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(320, 320);
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
}

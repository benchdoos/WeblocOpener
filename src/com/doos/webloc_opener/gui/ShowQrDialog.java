package com.doos.webloc_opener.gui;

import com.doos.commons.utils.FrameUtils;
import com.doos.webloc_opener.service.UrlsProceed;
import com.doos.webloc_opener.service.gui.MousePickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ShowQrDialog extends JDialog {
    BufferedImage qrCodeImage = null;
    private JPanel contentPane;
    private ImagePanel imagePanel;

    public ShowQrDialog(BufferedImage qrCodeImage) {
        this.qrCodeImage = qrCodeImage;

        setTitle("QR-Code for .webloc link");

        setContentPane(contentPane);
        setModal(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        imagePanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setSize(new Dimension(UrlsProceed.QR_CODE_WIDTH, UrlsProceed.QR_CODE_HEIGHT + 30));
        setResizable(false);

        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
    }

    private void createUIComponents() {
        imagePanel = new ImagePanel(qrCodeImage);
        MousePickListener mousePickListener = new MousePickListener(this);

        imagePanel.addMouseListener(mousePickListener.getMouseAdapter);
        imagePanel.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);
    }

    private void onCancel() {
        dispose();
    }
}

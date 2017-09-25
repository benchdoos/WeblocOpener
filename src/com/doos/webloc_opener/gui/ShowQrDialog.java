package com.doos.webloc_opener.gui;

import com.doos.commons.utils.FrameUtils;
import com.doos.webloc_opener.service.UrlsProceed;
import com.doos.webloc_opener.service.gui.MousePickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class ShowQrDialog extends JFrame {
    BufferedImage qrCodeImage = null;
    private JPanel contentPane;
    private ImagePanel imagePanel;

    public ShowQrDialog(BufferedImage qrCodeImage) {
        this.qrCodeImage = qrCodeImage;

        setTitle("QR-Code for .webloc");
        setIconImage(Toolkit.getDefaultToolkit().getImage(ShowQrDialog.class.getResource("/balloonIcon64.png")));


        setContentPane(contentPane);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        imagePanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(
                KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

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

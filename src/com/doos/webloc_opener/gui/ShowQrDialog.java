package com.doos.webloc_opener.gui;

import com.doos.commons.core.Translation;
import com.doos.commons.utils.FrameUtils;
import com.doos.webloc_opener.service.UrlsProceed;
import com.doos.webloc_opener.service.gui.MousePickListener;
import com.google.zxing.WriterException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ShowQrDialog extends JFrame {
    private String url;
    BufferedImage qrCodeImage = null;
    private JPanel contentPane;
    private ImagePanel imagePanel;
    private JButton openButton;
    String title = "QR-Code for .webloc";


    public ShowQrDialog(String url, BufferedImage qrCodeImage) throws IOException, WriterException {
        this.qrCodeImage = qrCodeImage;
        this.url = url;
        initGui();
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UrlsProceed.openUrl(url);
                dispose();
            }
        });
    }

    private void initGui() {
        translateDialog();

        setTitle(title);
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

        pack();
        setResizable(false);

        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
    }

    private void translateDialog() {
        Translation translation = new Translation("translations/showQrDialogBundle") {
            @Override
            public void initTranslations() {
                title = messages.getString("windowTitle");
            }
        };
        translation.initTranslations();
    }

    private void createUIComponents() {
        createImagePanel();
    }

    private void createImagePanel() {
        imagePanel = new ImagePanel(qrCodeImage);
        MousePickListener mousePickListener = new MousePickListener(this);

        imagePanel.addMouseListener(mousePickListener.getMouseAdapter);
        imagePanel.addMouseMotionListener(mousePickListener.getMouseMotionAdapter);
    }

    private void onCancel() {
        dispose();
    }
}

package com.doos.webloc_opener.gui;

import com.doos.commons.utils.FrameUtils;
import com.doos.webloc_opener.service.UrlsProceed;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InfoDialog extends JDialog {
    public String content;
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextPane textPane;

    public InfoDialog() {
        initGui();
    }

    private void initGui() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(AboutApplicationDialog.class.getResource("/infoIcon16.png")));
        setContentPane(contentPane);

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        textPane.setEditable(false);
        textPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    UrlsProceed.openUrl(e.getURL());
                }
            }
        });

        setSize(550, 300);
        setResizable(false);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    @Override
    public void setVisible(boolean b) {
        textPane.setText(content);
        super.setVisible(b);
    }
}

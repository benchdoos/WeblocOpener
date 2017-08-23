package com.doos.update_module.gui;

import com.doos.commons.utils.FrameUtils;
import com.doos.update_module.update.AppVersion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class UpdateInfoDialog extends JDialog {
    private AppVersion appVersion;
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextPane textPane;

    UpdateInfoDialog(AppVersion appVersion) {
        this.appVersion = appVersion;
        createGUI();
    }

    public static void main(String[] args) {
        AppVersion appVersion = new AppVersion();
        appVersion.setUpdateInfo("[<img src=\"https://benchdoos.github.io/img/1.3.1.png\" align=\"right\" />](https://benchdoos.github.io/)\n" +
                "- Url validation is more accurate\n" +
                "- Now you see if url is valid or not - valid urls are underlined and colored in blue, otherwise it is standart black.\n" +
                "- Now supported protocols are only <code>http://</code>, <code>https://</code>, and <code>ftp://</code>.\n");
        UpdateInfoDialog dialog = new UpdateInfoDialog(appVersion);
        System.exit(0);
    }

    private void createGUI() {
        setTitle("Info about update - " + appVersion.getVersion());
        setIconImage(Toolkit.getDefaultToolkit().getImage(UpdateInfoDialog.class.getResource("/info16.png")));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        getRootPane().registerKeyboardAction(e -> {
            onOK();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


        textPane.setText(generatePageForDisplay(appVersion.getUpdateInfo()));
        textPane.setHighlighter(null);

        textPane.registerKeyboardAction(e -> {
            onOK();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
        buttonOK.addActionListener(e -> onOK());

        setMinimumSize(new Dimension(550, 300));
        setSize(550, 300);


        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setVisible(true);
    }

    private String generatePageForDisplay(String updateInfo) {
        String style = "<style>" +
                "code {" +
                "    padding: 0;" +
                "    padding-top: 0.2em;" +
                "    padding-bottom: 0.2em;" +
                "    margin: 0;" +
                "    font-size: 85%;" +
                "    background-color: #f3f3f3;" +
                "    border-radius: 3px;" +
                "}" +
                "</style>";
        String defaultHead = "<html><head>" + style + "</head><body>";
        String defaultFooter = "</body></html>";
        updateInfo = updateInfo.replaceAll("\n", "<br>");

        return defaultHead + updateInfo + defaultFooter;
    }

    private void onOK() {
        dispose();
    }

}

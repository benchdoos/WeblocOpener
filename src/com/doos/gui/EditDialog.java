package com.doos.gui;

import com.doos.ApplicationConstants;
import com.doos.Main;
import com.doos.service.Analyzer;
import com.doos.service.UrlsProceed;
import com.doos.service.gui.ClickListener;
import com.doos.service.gui.WindowFocusRequester;
import com.doos.utils.FrameUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import static com.doos.service.Logging.getCurrentClassName;

public class EditDialog extends JFrame {
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    //JDialog dialog = this;

    String path = "";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextPane createWeblocFileTextPane;
    private JLabel iconLabel;

    @SuppressWarnings("unchecked")
    public EditDialog(String path) {

        log.debug("Got arguments: " + Arrays.toString(Main.args));

        this.path = path;
        setContentPane(contentPane);
        //setModal(true);
        setTitle("WeblocOpener - Edit .webloc link");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
                                               public void actionPerformed(ActionEvent e) {
                                                   onCancel();
                                               }
                                           }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        iconLabel.setToolTipText("Application version: " + ApplicationConstants.AppVersion);

        createWeblocFileTextPane.setBackground(new Color(232, 232, 232));
        createWeblocFileTextPane.setText(
                "<html>\n" +
                        "  <body>\n" +
                        "\tEdit <b>.webloc</b> link:\n" +
                        "  </body>\n" +
                        "</html>\n");

        Font font = textField1.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        textField1.setFont(font.deriveFont(attributes));

        textField1.addMouseListener(new ClickListener() {
            @Override
            public void doubleClick(MouseEvent e) {
                textField1.selectAll();
            }
        });

        try {
            URL url = new URL(Analyzer.takeUrl(new File(path)));
            textField1.setText(url.toString());
        } catch (Exception e) {
            log.warn("Can not read url from : " + path);
            textField1.setText("");
        }
        pack();

        setMinimumSize(getSize());
        setPreferredSize(getSize());
        setResizable(false); //TODO fix setMaximumSize

        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));

        log.debug("Got path path: [" + path + "]");
    }

    private void onOK() {
        try {
            URL url = new URL(textField1.getText());
            UrlsProceed.createWebloc(url, path);
            dispose();
        } catch (MalformedURLException e) {
            log.warn("Can not parse URL: [" + textField1.getText() + "]", e);
            FrameUtils.shakeFrame(this);
            JOptionPane.showMessageDialog(new Frame(), "Incorrect URL: [" + textField1.getText() + "]", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void onCancel() {
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        UrlsProceed.shutdownLogout();
        System.exit(0); //FIXME non-shutdown issue. Find out the shutdown issue mistake
    }


    @Override
    public void setVisible(boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WindowFocusRequester.runScript(WindowFocusRequester.requestFocusOnWindowScript(getTitle()));
            }
        });
        super.setVisible(b);
    }
}

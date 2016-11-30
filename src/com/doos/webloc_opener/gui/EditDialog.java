package com.doos.webloc_opener.gui;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.webloc_opener.service.UrlsProceed;
import com.doos.webloc_opener.service.gui.ClickListener;
import com.doos.webloc_opener.utils.FrameUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static com.doos.webloc_opener.service.Logging.getCurrentClassName;

public class EditDialog extends JFrame {
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    //JDialog dialog = this;

    private String path = "";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextPane createWeblocFileTextPane;
    private JLabel iconLabel;

    @SuppressWarnings("unchecked")
    public EditDialog(String pathToEditingFile) {

        setIconImage(Toolkit.getDefaultToolkit().getImage(EditDialog.class.getResource("/icon64.png")));

        this.path = pathToEditingFile;
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

        iconLabel.setToolTipText("Application version: " + ApplicationConstants.APP_VERSION);

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

        fillTextField(pathToEditingFile);

        pack();

        setMinimumSize(getSize());
        setPreferredSize(getSize());
        setResizable(false); //TODO fix setMaximumSize

        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));

        log.debug("Got path path: [" + pathToEditingFile + "]");
    }

    private void fillTextField(String pathToEditingFile) {
        try {
            URL url = new URL(UrlsProceed.takeUrl(new File(pathToEditingFile)));
            textField1.setText(url.toString());
        } catch (Exception e) {
            log.warn("Can not read url from : " + pathToEditingFile);

            fillTextFieldWithClipboard();
        }
    }

    private void fillTextFieldWithClipboard() {
        try {
            String data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            URL url = new URL(data);
            textField1.setText(url.toString());
        } catch (UnsupportedFlavorException | IOException ex) {
            textField1.setText("");
        }
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
    }


    @Override
    public void setVisible(boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //WindowFocusRequester.runScript(WindowFocusRequester.requestFocusOnWindowScript(getTitle())); //FIXME
            }
        });
        FrameUtils.bringToFront(this);
        super.setVisible(b);
    }
}

package com.doos.webloc_opener.gui;

import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.core.Translation;
import com.doos.commons.utils.FrameUtils;
import com.doos.webloc_opener.service.UrlsProceed;
import com.doos.webloc_opener.service.gui.ClickListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static com.doos.commons.utils.Logging.getCurrentClassName;

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
    private JLabel urlLabel;

    private String incorrectUrlMessage = "Incorrect URL";
    private String errorTitle = "Error";


    @SuppressWarnings("unchecked")
    public EditDialog(String pathToEditingFile) {

        setIconImage(Toolkit.getDefaultToolkit().getImage(EditDialog.class.getResource("/icon64.png")));

        this.path = pathToEditingFile;
        setContentPane(contentPane);

        translateDialog();

        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                if (textField1.getText().isEmpty()) {
                    fillTextFieldWithClipboard();
                }
                super.windowActivated(e);
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                           JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        createWeblocFileTextPane.setBackground(new Color(232, 232, 232));


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

    private void translateDialog() {
        Translation translation = new Translation("translations/EditDialogBundle") {
            @Override
            public void initTranslations() {
                setTitle(messages.getString("windowTitle"));
                urlLabel.setText(messages.getString("urlLabelText"));
                createWeblocFileTextPane.setText(
                        "<html>\n" +
                                "  <body>\n" + "\t"
                                + messages.getString("textPane1") + " <b>.webloc</b> "
                                + messages.getString("textPane2") + ":\n"
                                + "  </body>\n" +
                                "</html>\n");

                buttonOK.setText(messages.getString("buttonOk"));
                buttonCancel.setText(messages.getString("buttonCancel"));
                iconLabel.setToolTipText(messages.getString("iconLabel") + ApplicationConstants.APP_VERSION);
                incorrectUrlMessage = messages.getString("incorrectUrlMessage");
                errorTitle = messages.getString("errorTitle");
            }
        };
        translation.initTranslations();
    }

    private void fillTextField(String pathToEditingFile) {
        try {
            URL url = new URL(UrlsProceed.takeUrl(new File(pathToEditingFile)));
            textField1.setText(url.toString());
            log.debug("Got URL [" + url + "] from [" + pathToEditingFile + "]");
        } catch (Exception e) {
            log.warn("Can not read url from: [" + pathToEditingFile + "]");
            fillTextFieldWithClipboard();
        }
    }

    private void fillTextFieldWithClipboard() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            URL url = new URL(data);
            textField1.setText(url.toString());
            log.debug("Got URL from clipboard: " + url);
        } catch (UnsupportedFlavorException | IllegalStateException | HeadlessException | IOException e) {
            textField1.setText("");
            log.warn("Can not read URL from clipboard.", e);
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
            JOptionPane.showMessageDialog(this, incorrectUrlMessage + ": [" + textField1.getText() + "]", errorTitle,
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
        super.setVisible(b);
        FrameUtils.bringToFront(this);
    }
}

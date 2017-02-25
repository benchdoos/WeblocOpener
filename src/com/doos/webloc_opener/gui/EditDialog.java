package com.doos.webloc_opener.gui;

import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.core.Translation;
import com.doos.commons.utils.FrameUtils;
import com.doos.commons.utils.MessagePushable;
import com.doos.commons.utils.UserUtils;
import com.doos.webloc_opener.service.UrlsProceed;
import com.doos.webloc_opener.service.gui.ClickListener;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

public class EditDialog extends JFrame implements MessagePushable {
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
    private JTextPane errorTextPane;
    private JPanel errorPanel;

    private String incorrectUrlMessage = "Incorrect URL";
    private String errorTitle = "Error";
    private Timer messageTimer;


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


        textField1.addMouseListener(new ClickListener() {
            @Override
            public void doubleClick(MouseEvent e) {
                textField1.selectAll();
            }
        });

        textField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTextFont();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTextFont();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void updateTextFont() {
                UrlValidator urlValidator = new UrlValidator();
                if (urlValidator.isValid(textField1.getText())) {
                    if (textField1 != null) {
                        setTextFieldFont(textField1.getFont(), TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                        textField1.setForeground(Color.BLUE);
                    }
                } else {
                    if (textField1 != null) {
                        setTextFieldFont(textField1.getFont(), TextAttribute.UNDERLINE, -1);
                        textField1.setForeground(Color.BLACK);
                    }
                }
            }

        });

        fillTextField(pathToEditingFile);

        pack();

        /*setMinimumSize(getSize());
        setPreferredSize(getSize());*/

        setSize(300, 200);
        setResizable(false); //TODO fix setMaximumSize

        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));

        log.debug("Got path path: [" + pathToEditingFile + "]");
    }

    public void setTextFieldFont(Font font, TextAttribute attribute1, int attribute2) {
        Map attributes = font.getAttributes();
        attributes.put(attribute1, attribute2);
        textField1.setFont(font.deriveFont(attributes));
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
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(data)) {
                textField1.setText(url.toString());
                setTextFieldFont(textField1.getFont(), TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                log.debug("Got URL from clipboard: " + url);
            }
        } catch (UnsupportedFlavorException | IllegalStateException | HeadlessException | IOException e) {
            textField1.setText("");
            log.warn("Can not read URL from clipboard.", e);
        }
    }

    private void onOK() {
        try {
            URL url = new URL(textField1.getText());
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(textField1.getText())) {
                UrlsProceed.createWebloc(url, path);
                dispose();
            } else {
                throw new MalformedURLException();
            }
        } catch (MalformedURLException e) {
            log.warn("Can not parse URL: [" + textField1.getText() + "]", e);

            String message = incorrectUrlMessage + ": [";
            String incorrectUrl = textField1.getText()
                    .substring(0, Math.min(textField1.getText().length(), 10));
            //Fixes EditDialog long url message showing issue
            message += textField1.getText().length() > incorrectUrl.length() ? incorrectUrl + "...]" : incorrectUrl + "]";


            UserUtils.showWarningMessageToUser(this, errorTitle,
                    message);
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

    @Override
    public void showMessage(String message, int messageValue) {
        errorPanel.setBackground(MessagePushable.getMessageColor(messageValue));

        boolean wasVisible = errorPanel.isVisible();
        errorPanel.setVisible(true);
        errorTextPane.setText(message);

        if (!wasVisible) {
            updateSize(SettingsDialog.UpdateMode.BEFORE_HIDE);
        }

        if (messageTimer != null) {
            messageTimer.stop();
        }

        messageTimer = new Timer(DEFAULT_TIMER_DELAY, e -> {
            errorTextPane.setText("");
            errorPanel.setVisible(false);
            updateSize(SettingsDialog.UpdateMode.AFTER_HIDE);
        });
        messageTimer.setRepeats(false);
        messageTimer.start();
    }

    private void updateSize(SettingsDialog.UpdateMode mode) {

        setResizable(true);

        revalidate();
        final int DEFAULT_APPLICATION_WIDTH = 295;
        if (mode == SettingsDialog.UpdateMode.BEFORE_HIDE) {
            pack();
            setSize(new Dimension(DEFAULT_APPLICATION_WIDTH, getHeight()));
        } else if (mode == SettingsDialog.UpdateMode.AFTER_HIDE) {
            setSize(new Dimension(DEFAULT_APPLICATION_WIDTH, 207));
        }
        setResizable(false);

    }

    enum UpdateMode {BEFORE_HIDE, AFTER_HIDE}
}

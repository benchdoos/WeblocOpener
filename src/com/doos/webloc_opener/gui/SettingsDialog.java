package com.doos.webloc_opener.gui;

import com.doos.commons.core.Translation;
import com.doos.commons.registry.RegistryCanNotReadInfoException;
import com.doos.commons.registry.RegistryCanNotWriteInfoException;
import com.doos.commons.registry.RegistryException;
import com.doos.commons.registry.RegistryManager;
import com.doos.commons.registry.fixer.RegistryFixer;
import com.doos.commons.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerInstallPathKeyFailException;
import com.doos.commons.utils.FrameUtils;
import com.doos.commons.utils.MessagePushable;
import com.doos.commons.utils.UserUtils;
import com.doos.commons.utils.browser.Browser;
import com.doos.commons.utils.browser.BrowserManager;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.MinimalBalloonStyle;
import net.java.balloontip.utils.TimingUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.doos.commons.utils.Logging.getCurrentClassName;

public class SettingsDialog extends JFrame implements MessagePushable {
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    boolean onInit = true;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox autoUpdateEnabledCheckBox;
    private JButton updateNowButton;
    private JLabel versionLabel;
    private JLabel versionStringLabel;
    private JButton aboutButton;
    private JTextPane errorTextPane;
    private JPanel errorPanel;
    private JComboBox<Object> comboBox;
    private JButton updateListButton;
    private JTextField callTextField;
    private JLabel callLabel;
    private JLabel syntaxInfoLabel;
    private JCheckBox incognitoCheckBox;
    private String errorMessageTitle = "Error";
    private String canNotSaveSettingsToRegistryMessage = "Can not save settings to registry.";
    private Timer messageTimer;
    private String toolTipText = "<html><body style=\"font-size:10px;\">Syntax: <b><u>file path</u></b> <b style=\"color:red;\">%site</b>, don't forget to add <b>%site</b><br>" +
            "Example for Google Chrome: <b style=\"color:green;\">start chrome \"%site\"</b></body></html>";

    private String chooseAFile = "Choose a file:";

    private String customBrowserName = "Custom...";

    public SettingsDialog() {
        log.debug("Creating settings dialog.");
        translateDialog();
        initGui();
        log.debug("Settings dialog created.");
    }

    private int findBrowser(String browserValue) {
        int result = 0;
        for (int i = 0; i < BrowserManager.getBrowserList().size(); i++) {
            Browser browser = BrowserManager.getBrowserList().get(i);
            System.out.println(browser);

            if (browser.getCall() != null) {
                if (browser.getCall().equals(browserValue)) {
                    result = i;
                    return result;
                } else if (browser.getIncognitoCall() != null) {
                    if (browser.getIncognitoCall().equals(browserValue)) {
                        result = i;
                        return result;
                    }
                }
            }
        }

        if (browserValue.equals("default") || browserValue.isEmpty()) {
            return 0;
        } else return BrowserManager.getBrowserList().size() - 1;
    }

    private void initComboBox() {
        ArrayList<Browser> browsers = BrowserManager.getBrowserList();

        Browser others = new Browser(customBrowserName, null);
        browsers.add(others);

        comboBox.setModel(new DefaultComboBoxModel<>(browsers.toArray()));

        comboBox.addActionListener(e -> {
            if (comboBox.getSelectedIndex() == comboBox.getItemCount() - 1) {
                if (!onInit) {
                    log.info("Opening file browser for custom browser search");
                    String path = openFileBrowser();
                    if (path != null) {
                        callLabel.setVisible(true);
                        callTextField.setVisible(true);
                        callTextField.setText(path);
                        incognitoCheckBox.setEnabled(false);
                    }
                } else {
                    callLabel.setVisible(true);
                    callTextField.setText(RegistryManager.getBrowserValue());
                    callTextField.setVisible(true);
                    syntaxInfoLabel.setVisible(true);
                }
            } else {
                if (comboBox.getSelectedIndex() == 0) {
                    incognitoCheckBox.setEnabled(false);
                    incognitoCheckBox.setSelected(false);
                } else {
                    if (browsers.get(comboBox.getSelectedIndex()).getIncognitoCall() != null) {
                        incognitoCheckBox.setEnabled(true);
                    } else {
                        incognitoCheckBox.setSelected(false);
                        incognitoCheckBox.setEnabled(false);
                    }
                }
                callLabel.setVisible(false);
                callTextField.setVisible(false);
            }
        });
    }

    private void initGui() {
        setContentPane(contentPane);


        getRootPane().setDefaultButton(buttonOK);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/balloonIcon64.png")));

        syntaxInfoLabel.setVisible(false);
        updateListButton.setVisible(false);
        callTextField.setVisible(false);
        callLabel.setVisible(false);

        initComboBox();

        loadSettings();

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        updateNowButton.addActionListener(e -> onUpdateNow());

        aboutButton.addActionListener(e -> onAbout());

        callTextField.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                syntaxInfoLabel.setVisible(false);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                syntaxInfoLabel.setVisible(true);
            }
        });

        setSyntaxInfoButtonToolTip();

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        try {
            versionLabel.setText(RegistryManager.getAppVersionValue());
        } catch (RegistryCanNotReadInfoException e) {
            versionLabel.setText("Unknown");
        }

        onInit = false;

        pack();
        setSize(400, 250);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setResizable(false);
    }

    private void loadSettings() {
        try {
            autoUpdateEnabledCheckBox.setSelected(RegistryManager.isAutoUpdateActive());
            comboBox.setSelectedIndex(findBrowser(RegistryManager.getBrowserValue()));
            final Browser browser = (Browser) comboBox.getSelectedItem();

            if (browser != null) {
                if (browser.getIncognitoCall() != null) {
                    incognitoCheckBox.setSelected(RegistryManager.getBrowserValue().equals(browser.getIncognitoCall()));
                } else {
                    incognitoCheckBox.setSelected(false);
                    incognitoCheckBox.setEnabled(false);
                }
            } else {
                incognitoCheckBox.setSelected(false);
                incognitoCheckBox.setEnabled(false);
            }

        } catch (RegistryException e) {
            log.warn("Can not load data from registry", e);
            try {
                RegistryFixer.fixRegistry();
            } catch (FileNotFoundException | RegistryFixerAutoUpdateKeyFailException | RegistryFixerAppVersionKeyFailException e1) {
                RegistryManager.setDefaultSettings(); //To prevent crash
                try {
                    autoUpdateEnabledCheckBox.setSelected(RegistryManager.isAutoUpdateActive());
                } catch (RegistryCanNotReadInfoException ignore) {
                }
            } catch (RegistryFixerInstallPathKeyFailException e1) {
                log.warn("Can not fix install key ", e1);
            }
        }
    }

    private void onAbout() {
        AboutApplicationDialog dialog = new AboutApplicationDialog();
        dialog.setVisible(true);
    }

    private void onCancel() {
        dispose();
    }

    private void onOK() {
        try {
            updateRegistryAndDispose();
        } catch (RegistryException e) {
            log.warn("Can not save settings: " + RegistryManager.KEY_AUTO_UPDATE, e);
            try {
                RegistryFixer.fixRegistryAnyway();
                updateRegistryAndDispose();
            } catch (FileNotFoundException | RegistryException e1) {
                log.error("Can not fix registry", e1);
                UserUtils.showWarningMessageToUser(this, errorMessageTitle, canNotSaveSettingsToRegistryMessage);
            } catch (Exception e1) {
                log.warn("Can not update settings", e1);
            }
        }
    }

    public static void runUpdater() {
        String run;
        try {
            run = "java -jar \"" + RegistryManager.getInstallLocationValue() + "Updater.jar\"";
        } catch (RegistryCanNotReadInfoException e) {
            run = new File(SettingsDialog.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath()).getAbsolutePath().replace("%20", " ");
            log.warn("Can not read registry, using alternate path: [" + run + "]", e);
        }
        log.info("Running: " + run);
        try {
            Runtime.getRuntime().exec(run);
        } catch (IOException e) {
            log.warn("Can not execute command: " + run, e);
        }
    }

    private void onUpdateNow() {
        runUpdater();
        dispose();
    }

    private String openFileBrowser() {
        log.debug("Opening File Browser");

        FileDialog fd = new FileDialog(this, chooseAFile, FileDialog.LOAD);
        fd.setIconImage(Toolkit.getDefaultToolkit()
                .getImage(SettingsDialog.class.getResource("/balloonIcon64.png")));
        fd.setDirectory(System.getProperty("user.dir"));
        fd.setFile("*.exe");
        fd.setMultipleMode(false);
        fd.setVisible(true);
        String filename = fd.getFile();
        File[] f = fd.getFiles();
        if (f.length > 0) {
            log.debug("Choice: " + fd.getFiles()[0].getAbsolutePath());
            return fd.getFiles()[0].getAbsolutePath();
        } else {
            log.debug("Choice canceled");
            return null;
        }
    }

    private BalloonTip generateBalloonTip(String toolTipText) {
        BalloonTip balloonTip = new BalloonTip(syntaxInfoLabel, toolTipText);
        balloonTip.setStyle(new MinimalBalloonStyle(Color.white, 5));
        balloonTip.setCloseButton(null);
        balloonTip.setVisible(false);
        balloonTip.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                balloonTip.setVisible(false);
            }
        });
        return balloonTip;
    }

    private void setSyntaxInfoButtonToolTip() {

        syntaxInfoLabel.addMouseListener(new MouseAdapter() {
            final int DEFAULT_TIME = 10_000;
            final int SHORT_TIME = 6_000;

            BalloonTip balloonTip = generateBalloonTip(toolTipText);

            @Override
            public void mouseClicked(MouseEvent e) {
                balloonTip.setVisible(true);
                TimingUtils.showTimedBalloon(balloonTip, DEFAULT_TIME);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                balloonTip.setVisible(true);
                TimingUtils.showTimedBalloon(balloonTip, SHORT_TIME);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                balloonTip = generateBalloonTip(toolTipText);
            }
        });


    }

    @Override
    public void showMessage(String message, int messageValue) {
        errorPanel.setBackground(MessagePushable.getMessageColor(messageValue));

        boolean wasVisible = errorPanel.isVisible();
        errorPanel.setVisible(true);
        errorTextPane.setText(message);

        if (!wasVisible) {
            updateSize(UpdateMode.BEFORE_HIDE);
        }

        if (messageTimer != null) {
            messageTimer.stop();
        }

        messageTimer = new Timer(DEFAULT_TIMER_DELAY, e -> {
            errorTextPane.setText("");
            errorPanel.setVisible(false);
            updateSize(UpdateMode.AFTER_HIDE);
        });
        messageTimer.setRepeats(false);
        messageTimer.start();
    }

    private void translateDialog() {
        Translation translation = new Translation("translations/SettingsDialogBundle") {
            @Override
            public void initTranslations() {
                setTitle(messages.getString("windowTitle"));

                buttonOK.setText(messages.getString("buttonOk"));
                buttonCancel.setText(messages.getString("buttonCancel"));

                versionStringLabel.setText(messages.getString("versionString"));
                autoUpdateEnabledCheckBox.setText(messages.getString("autoUpdateEnabledCheckBox"));
                updateNowButton.setText(messages.getString("updateNowButton"));

                canNotSaveSettingsToRegistryMessage = messages.getString("canNotSaveSettingsToRegistryMessage");
                errorMessageTitle = messages.getString("errorMessage");

                toolTipText = messages.getString("toolTipText");

                customBrowserName = messages.getString("customBrowserName");
                chooseAFile = messages.getString("chooseAFile");
            }
        };
        translation.initTranslations();
    }

    private void updateRegistryAndDispose() throws RegistryCanNotReadInfoException, RegistryCanNotWriteInfoException {
        if (RegistryManager.isAutoUpdateActive() != autoUpdateEnabledCheckBox.isSelected()) {
            RegistryManager.setAutoUpdateActive(autoUpdateEnabledCheckBox.isSelected());
        }
        Browser browser = (Browser) comboBox.getSelectedItem();
        if (browser != null) {
            log.info("browser call: " + browser.getCall());
            if (comboBox.getSelectedIndex() != comboBox.getItemCount() - 1) {
                if (browser.getCall() != null) {
                    if (!RegistryManager.getBrowserValue().equals(browser.getCall())) {
                        if (!incognitoCheckBox.isSelected()) {
                            RegistryManager.setBrowserValue(browser.getCall());
                        }
                    }
                }
                if (browser.getIncognitoCall() != null) {
                    if (!RegistryManager.getBrowserValue().equals(browser.getIncognitoCall())) {
                        if (incognitoCheckBox.isSelected()) {
                            RegistryManager.setBrowserValue(browser.getIncognitoCall());
                        }
                    }
                }
            } else {
                if (!callTextField.getText().equals(browser.getIncognitoCall())) {
                    RegistryManager.setBrowserValue(callTextField.getText());
                }
            }
        }

        dispose();
    }

    private void updateSize(UpdateMode mode) {

        setResizable(true);
        revalidate();
        final int DEFAULT_APPLICATION_WIDTH = 350;
        if (mode == UpdateMode.BEFORE_HIDE) {
            pack();
            setSize(new Dimension(DEFAULT_APPLICATION_WIDTH, getHeight()));
        } else if (mode == UpdateMode.AFTER_HIDE) {
            setSize(new Dimension(DEFAULT_APPLICATION_WIDTH, 200));
        }
        setResizable(false);

    }

    enum UpdateMode {BEFORE_HIDE, AFTER_HIDE}
}

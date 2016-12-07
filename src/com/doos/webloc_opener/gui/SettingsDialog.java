package com.doos.webloc_opener.gui;

import com.doos.commons.core.Translation;
import com.doos.commons.registry.RegistryCanNotReadInfoException;
import com.doos.commons.registry.RegistryException;
import com.doos.commons.registry.RegistryManager;
import com.doos.commons.registry.fixer.RegistryFixer;
import com.doos.commons.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.doos.commons.registry.fixer.RegistryFixerInstallPathKeyFailException;
import com.doos.commons.utils.FrameUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.doos.commons.utils.Logging.getCurrentClassName;

public class SettingsDialog extends JFrame {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox autoUpdateEnabledCheckBox;
    private JButton updateNowButton;
    private JLabel versionLabel;
    private JLabel versionStringLabel;
    private JButton aboutButton;


    private String errorMessage = "Error";
    private String canNotSaveSettingsToRegistryMessage = "Can not save settings to registry.";

    public SettingsDialog() {
        setContentPane(contentPane);


        getRootPane().setDefaultButton(buttonOK);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/balloonIcon64.png")));

        loadSettings();

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        updateNowButton.addActionListener(e -> onUpdateNow());

        aboutButton.addActionListener(e -> {
            onAbout();
        });

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

        pack();
        setSize(350, 200);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setResizable(false);
        translateDialog();
    }

    private void onAbout() {
        AboutApplicationDialog dialog = new AboutApplicationDialog();
        dialog.setVisible(true);
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
                errorMessage = messages.getString("errorMessage");
            }
        };
        translation.initTranslations();
    }

    private void onUpdateNow() {
        String run;
        try {
            run = "java -jar \"" + RegistryManager.getInstallLocationValue()
                    + File.separator + "Updater.jar\"";

        } catch (RegistryCanNotReadInfoException e) {
            e.printStackTrace();
            run = new File(SettingsDialog.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath()).getAbsolutePath().replace("%20", " ");
        }
        log.info("Running " + run);
        try {
            Runtime.getRuntime().exec(run);
        } catch (IOException e) {
            log.warn("Can not execute command: " + run, e);
        }
        dispose();
    }

    private void loadSettings() {
        try {
            autoUpdateEnabledCheckBox.setSelected(RegistryManager.isAutoUpdateActive());
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

    private void onOK() {
        try {
            if (RegistryManager.isAutoUpdateActive() != autoUpdateEnabledCheckBox.isSelected()) {
                RegistryManager.setAutoUpdateActive(autoUpdateEnabledCheckBox.isSelected());
            }
        } catch (RegistryException e) {
            log.warn("Can not save settings change", e);
            JOptionPane.showMessageDialog(null, errorMessage,
                    canNotSaveSettingsToRegistryMessage,
                    JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}

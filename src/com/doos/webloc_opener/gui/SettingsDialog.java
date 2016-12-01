package com.doos.webloc_opener.gui;

import com.doos.settings_manager.Translatable;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryException;
import com.doos.settings_manager.registry.RegistryManager;
import com.doos.settings_manager.registry.fixer.RegistryFixer;
import com.doos.settings_manager.registry.fixer.RegistryFixerAppVersionKeyFailException;
import com.doos.settings_manager.registry.fixer.RegistryFixerAutoUpdateKeyFailException;
import com.doos.settings_manager.registry.fixer.RegistryFixerInstallPathKeyFailException;
import com.doos.webloc_opener.core.Main;
import com.doos.webloc_opener.utils.FrameUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.doos.webloc_opener.service.Logging.getCurrentClassName;

public class SettingsDialog extends JFrame implements Translatable {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox autoUpdateEnabledCheckBox;
    private JButton updateNowButton;
    private JLabel versionLabel;
    private JLabel versionStringLabel;


    private String errorMessage = "Error";
    private String canNotSaveSettingsToRegistryMessage = "Can not save settings to registry.";


    public SettingsDialog() {
        setContentPane(contentPane);

        initTranslations();


        getRootPane().setDefaultButton(buttonOK);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/balloonIcon64.png")));

        loadSettings();

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

        updateNowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUpdateNow();
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
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        try {
            versionLabel.setText(RegistryManager.getAppVersionValue());
        } catch (RegistryCanNotReadInfoException e) {
            versionLabel.setText("Unknown");
        }

        pack();
        setSize(350, 200);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setResizable(false);
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
        System.out.println(">>>> " + run);
        try {
            Runtime.getRuntime().exec(run);
        } catch (IOException e) {
            e.printStackTrace();
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
            RegistryManager.setAutoUpdateActive(autoUpdateEnabledCheckBox.isSelected());
        } catch (RegistryException e) {
            log.warn("Can not save settings change", e);
            JOptionPane.showMessageDialog(new JFrame(), errorMessage,
                                          canNotSaveSettingsToRegistryMessage,
                                          JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    @Override
    public void initTranslations() {
        ResourceBundle messages = Main.getTranslation("translations/SettingsDialogBundle");
        setTitle(messages.getString("windowTitle"));

        buttonOK.setText(messages.getString("buttonOk"));
        buttonCancel.setText(messages.getString("buttonCancel"));

        versionStringLabel.setText(messages.getString("versionString"));
        autoUpdateEnabledCheckBox.setText(messages.getString("autoUpdateEnabledCheckBox"));
        updateNowButton.setText(messages.getString("updateNowButton"));

        canNotSaveSettingsToRegistryMessage = messages.getString("canNotSaveSettingsToRegistryMessage");
        errorMessage = messages.getString("errorMessage");
    }
}

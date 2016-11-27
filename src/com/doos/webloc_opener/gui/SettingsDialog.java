package com.doos.webloc_opener.gui;

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

import static com.doos.webloc_opener.core.Main.properties;
import static com.doos.webloc_opener.service.Logging.getCurrentClassName;

public class SettingsDialog extends JFrame {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox autoUpdateEnabledCheckBox;
    private JButton updateNowButton;
    private JLabel versionLabel;

    public SettingsDialog() {
        setContentPane(contentPane);
        setTitle("WeblocOpener - Settings");
        getRootPane().setDefaultButton(buttonOK);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/icon64.png")));

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
            Main.loadProperties();
            autoUpdateEnabledCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty(RegistryManager.KEY_AUTO_UPDATE)));
        } catch (RegistryException e) {
            log.warn("Can not load data from registry", e);
            try {
                properties = RegistryFixer.fixRegistry();
            } catch (FileNotFoundException | RegistryFixerAutoUpdateKeyFailException | RegistryFixerAppVersionKeyFailException e1) {
                Main.useDefaultAppProperties(); //To prevent crash
            } catch (RegistryFixerInstallPathKeyFailException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void onOK() {
        properties.setProperty(RegistryManager.KEY_AUTO_UPDATE, autoUpdateEnabledCheckBox.isSelected() + "");
        try {
            Main.saveProperties();
        } catch (RegistryException e) {
            log.warn("Can not save settings change", e);
            JOptionPane.showMessageDialog(new JFrame(), "Error",
                                          "Can not save settings to registry.",
                                          JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}

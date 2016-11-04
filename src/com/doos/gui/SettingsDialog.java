package com.doos.gui;

import com.doos.ApplicationConstants;
import com.doos.utils.FrameUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.doos.service.Logging.getCurrentClassName;

public class SettingsDialog extends JFrame {
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    final String UPDATE_ACTIVE = "UPDATE_ACTIVE";
    final String LAST_UPDATED = "LAST_UPDATED";
    final String CURRENT_APP_VERSION = "CURRENT_APP_VERSION";
    Properties properties = new Properties();
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox autoUpdateEnabledCheckBox;
    private JLabel lastUpdateDateLable;
    private JButton updateNowButton;

    public SettingsDialog() {
        setContentPane(contentPane);
        setTitle("WeblocOpener - Settings");
        getRootPane().setDefaultButton(buttonOK);
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

        pack();
        setSize(350, 200);
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setResizable(false);
    }

    private void onUpdateNow() {
        //TODO add here run of com.doos.update module.
    }

    private void loadSettings() {
        log.info("Loading settings from: " + ApplicationConstants.SETTINGS_FILE_PATH);
        try {
            properties.load(new FileInputStream(ApplicationConstants.SETTINGS_FILE_PATH));
            autoUpdateEnabledCheckBox.setSelected(properties.getProperty(UPDATE_ACTIVE).equals("true"));
            lastUpdateDateLable.setText(properties.getProperty(LAST_UPDATED));
        } catch (FileNotFoundException e) {
            log.warn("Can not load property file: " + ApplicationConstants.SETTINGS_FILE_PATH, e);
            log.info("Creating a new property file");
            properties.setProperty(UPDATE_ACTIVE, "true");
            properties.setProperty(LAST_UPDATED, "none");
            properties.setProperty(CURRENT_APP_VERSION, ApplicationConstants.APP_VERSION);
            try {
                properties.store(new FileOutputStream(ApplicationConstants.SETTINGS_FILE_PATH), "Created new");
                loadSettings();
            } catch (IOException e1) {
                log.warn("Can not create a settings file", e1);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }


    private void onOK() {
        properties.setProperty(UPDATE_ACTIVE, autoUpdateEnabledCheckBox.isSelected() + "");
        try {
            properties.setProperty(CURRENT_APP_VERSION, ApplicationConstants.APP_VERSION);
            properties.store(new FileOutputStream(ApplicationConstants.SETTINGS_FILE_PATH), "Updated");
        } catch (IOException e) {
            log.warn("Can not save settings file", e);
            JOptionPane.showMessageDialog(new JFrame(), "Error", "Can not save settings!\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}

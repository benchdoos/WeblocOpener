package com.doos.gui;

import com.doos.core.Main;
import com.doos.update.AppVersion;
import com.doos.update.Updater;
import com.doos.utils.ApplicationConstants;
import com.doos.utils.FrameUtils;
import com.doos.utils.Internal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.doos.core.Main.*;

public class UpdateDialog extends JFrame {
    AppVersion serverAppVersion;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JProgressBar progressBar1;
    private JLabel currentVersionLabel;
    private JLabel availableVersionLabel;
    private JLabel newVersionReleaseDateLabel;
    private JLabel lastUpdatedLabel;
    private JLabel newVersionSizeLable;

    public UpdateDialog() {
        serverAppVersion = new AppVersion();

        setContentPane(contentPane);
        setTitle("Update - WeblocOpener");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        onOK();
                    }

                }).start();
            }

        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setSize(new Dimension(400, 170));
        setResizable(false);
        loadProperties();
    }

    public void checkForUpdates() {
        progressBar1.setIndeterminate(true);
        Updater updater = new Updater();
        serverAppVersion = updater.getAppVersion();
        progressBar1.setIndeterminate(false);
        availableVersionLabel.setText(serverAppVersion.getVersion());
        newVersionSizeLable.setText(serverAppVersion.getSize() / 1024 + "");
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        newVersionReleaseDateLabel.setText(dateFormatter.format(serverAppVersion.getUpdateDate()));


        String str = properties.getProperty(CURRENT_APP_VERSION);
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) < 0) {
            //Need to update
            buttonOK.setEnabled(true);
        } else if (Internal.versionCompare(str, serverAppVersion.getVersion()) > 0) {
            //App version is bigger then on server
            buttonOK.setText("Hello, dev!");
            buttonOK.setEnabled(false);
        } else if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
            //No reason to update
            buttonOK.setText("Version is up to date");
        }
    }

    private void loadProperties() {
        try {
            properties.load(new FileInputStream(ApplicationConstants.SETTINGS_FILE_PATH));
            lastUpdatedLabel.setText(properties.getProperty(Main.LAST_UPDATED));
            currentVersionLabel.setText(properties.getProperty(CURRENT_APP_VERSION));
        } catch (IOException e) {
            e.getStackTrace();
            lastUpdatedLabel.setText("Unknown");
            currentVersionLabel.setText("Unknown");
        }
        availableVersionLabel.setText("Unknown");
        newVersionReleaseDateLabel.setText("Unknown");
    }


    private void onOK() {
        buttonOK.setEnabled(false);
        int successUpdate = Updater.startUpdate(serverAppVersion, progressBar1);
        buttonOK.setEnabled(true);

        switch (successUpdate) {
            case 0: //Normal state, app updated
                properties.setProperty(CURRENT_APP_VERSION, serverAppVersion.getVersion());
                DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
                properties.setProperty(LAST_UPDATED, dateFormatter.format(Calendar.getInstance().getTime()));

                for (String pname : properties.stringPropertyNames()) {
                    System.out.println("PROP:>" + pname + " " + properties.getProperty(pname));
                }
                Main.updateProperties();
                JOptionPane.showMessageDialog(this, "WeblocOpener successfully updated to version: "
                        + serverAppVersion.getVersion(), "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                break;
            case 1: //Installation was cancelled or Incorrect function.
                JOptionPane.showMessageDialog(this,
                        "Installation cancelled by User during installation",
                        "Installation cancelled", JOptionPane.WARNING_MESSAGE);
                break;
            case 2: //The system cannot find the file specified. OR! User gave no permissions.
                JOptionPane.showMessageDialog(this,
                        "Installation can not be run, because it has no permissions.",
                        "Installation cancelled", JOptionPane.WARNING_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Installation cancelled by Error (unhandled error),"
                                + "\ncode: " + successUpdate
                                + "\nvisit https://github.com/benchdoos/WeblocOpener for more info.",
                        "Installation cancelled", JOptionPane.ERROR_MESSAGE);
                break;
        }

        //dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}

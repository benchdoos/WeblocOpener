package com.doos.update_module.gui;

import com.doos.settings_manager.registry.RegistryException;
import com.doos.settings_manager.registry.RegistryManager;
import com.doos.update_module.core.Main;
import com.doos.update_module.update.AppVersion;
import com.doos.update_module.update.Updater;
import com.doos.update_module.utils.FrameUtils;
import com.doos.update_module.utils.Internal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UpdateDialog extends JFrame {
    private AppVersion serverAppVersion;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JProgressBar progressBar1;
    private JLabel currentVersionLabel;
    private JLabel availableVersionLabel;
    private JLabel newVersionSizeLable;

    private Thread updateThread;

    public UpdateDialog() {
        serverAppVersion = new AppVersion();

        setContentPane(contentPane);
        setTitle("Update - WeblocOpener");
        getRootPane().setDefaultButton(buttonOK);

        setIconImage(Toolkit.getDefaultToolkit().getImage(UpdateDialog.class.getResource("/icon.png")));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        onOK();
                    }

                });
                updateThread.start();
            }

        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

        String str = Main.properties.getProperty(RegistryManager.KEY_CURRENT_VERSION);
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
            Main.loadProperties();
            currentVersionLabel.setText(Main.properties.getProperty(RegistryManager.KEY_CURRENT_VERSION));
        } catch (RegistryException e) {
            e.getStackTrace();
            currentVersionLabel.setText("Unknown");
        }
        availableVersionLabel.setText("Unknown");
    }


    private void onOK() {
        buttonOK.setEnabled(false);
        if (!Thread.currentThread().isInterrupted()) {
            int successUpdate = Updater.startUpdate(serverAppVersion, progressBar1);
            buttonOK.setEnabled(true);

            if (!Thread.currentThread().isInterrupted()) {
                switch (successUpdate) {
                    case 0: //NORMAL state, app updated
                        Main.properties.setProperty(RegistryManager.KEY_CURRENT_VERSION, serverAppVersion.getVersion());

                        for (String pname : Main.properties.stringPropertyNames()) {
                            System.out.println("PROP:>" + pname + " " + Main.properties.getProperty(pname));
                        }
                        Main.updateProperties();
                        JOptionPane.showMessageDialog(this, "WeblocOpener successfully updated to version: "
                                + serverAppVersion.getVersion(), "Success", JOptionPane.INFORMATION_MESSAGE);

                        if (Main.mode != Main.Mode.AFTER_UPDATE) {
                            try {
                                String value = RegistryManager.getInstallLocationValue();
                                Runtime.getRuntime().exec("java -jar \"" + value + "Updater.jar\" -afterUpdate");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        dispose();
                        break;
                    case 1: //Installation was cancelled or Incorrect function or corrupt file
                        JOptionPane.showMessageDialog(this,
                                "Installation cancelled by User during installation",
                                "Installation cancelled", JOptionPane.WARNING_MESSAGE);
                        Updater.installerFile.delete();
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
            }
        } else {
            buttonOK.setEnabled(false);
        }

        //dispose();
    }


    private void onCancel() {
        // add your code here if necessary
        if (updateThread != null) {
            if (!updateThread.isInterrupted()) {
                updateThread.interrupt();
                System.out.println(updateThread.isInterrupted());
            }
        }
        dispose();
    }

    public AppVersion getAppVersion() {
        return serverAppVersion;
    }
}

package gui;

import core.Main;
import update.AppVersion;
import update.Updater;
import utils.ApplicationConstants;
import utils.FrameUtils;
import utils.Internal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static core.Main.*;

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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
            buttonOK.setEnabled(true);
        }
        //TODO DELETE THIS AFTER TESTS
       /* if (Internal.versionCompare(str, serverAppVersion.getVersion()) > 0) {
            buttonOK.setEnabled(true);
        }*/////

        //TODO DELETE THIS AFTER TESTS
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
            buttonOK.setText("Version is up to date");
            /*buttonOK.setEnabled(true);*/
        }////
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
        int successUpdate = Updater.startUpdate(serverAppVersion, progressBar1);
        JFrame optionPaneFrame = new JFrame();
        optionPaneFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        switch (successUpdate) {
            case 0: //Normal state, app updated
                properties.setProperty(CURRENT_APP_VERSION, serverAppVersion.getVersion());
                DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
                properties.setProperty(LAST_UPDATED, dateFormatter.format(Calendar.getInstance().getTime()));

                for (String pname : properties.stringPropertyNames()) {
                    System.out.println("PROP:>" + pname + " " + properties.getProperty(pname));
                }
                Main.updateProperties();
                JOptionPane.showMessageDialog(optionPaneFrame, "WeblocOpener successfully updated to version: "
                                + serverAppVersion.getVersion(), "Success", JOptionPane.INFORMATION_MESSAGE,
                        UIManager.getIcon("OptionPane.informationIcon"));
                dispose();
                break;
            case 1: //Installation was cancelled or Incorrect function.
                JOptionPane.showMessageDialog(optionPaneFrame,
                        "Installation cancelled by User during installation",
                        "Installation cancelled", JOptionPane.WARNING_MESSAGE);
                break;
            case 2: //The system cannot find the file specified. OR! User gave no permissions.
                JOptionPane.showMessageDialog(optionPaneFrame,
                        "Installation can not be run, because it has no permissions.",
                        "Installation cancelled", JOptionPane.WARNING_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(optionPaneFrame,
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

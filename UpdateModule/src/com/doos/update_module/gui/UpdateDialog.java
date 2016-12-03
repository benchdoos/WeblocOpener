package com.doos.update_module.gui;

import com.doos.settings_manager.ApplicationConstants;
import com.doos.settings_manager.Translation;
import com.doos.settings_manager.core.SettingsManager;
import com.doos.settings_manager.registry.RegistryCanNotReadInfoException;
import com.doos.settings_manager.registry.RegistryCanNotWriteInfoException;
import com.doos.settings_manager.registry.RegistryException;
import com.doos.settings_manager.registry.RegistryManager;
import com.doos.settings_manager.utils.FrameUtils;
import com.doos.update_module.core.Main;
import com.doos.update_module.update.AppVersion;
import com.doos.update_module.update.Updater;
import com.doos.update_module.utils.Internal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import static com.doos.settings_manager.utils.UserUtils.showErrorMessageToUser;

@SuppressWarnings({"ALL", "ResultOfMethodCallIgnored"})
public class UpdateDialog extends JFrame {
    public static UpdateDialog updateDialog = null;

    public JProgressBar progressBar1;
    public JButton buttonOK;
    public JButton buttonCancel;
    private Translation translation;
    private AppVersion serverAppVersion;
    private JPanel contentPane;
    private JLabel currentVersionLabel;
    private JLabel availableVersionLabel;
    private JLabel newVersionSizeLabel;
    private JLabel unitLabel;
    private JLabel currentVersionStringLabel;
    private JLabel avaliableVersionStringLabel;
    private Thread updateThread;
    private String successUpdatedMessage = "WeblocOpener successfully updated to version: ";
    private String successTitle = "Success";

    private String installationCancelledTitle = "Installation cancelled";
    private String installationCancelledMessage = "Installation cancelled by User during installation";

    private String noPermissionsMessage = "Installation can not be run, because it has no permissions.";

    private String installationCancelledByErrorMessage1 = "Installation cancelled by Error (unhandled error),";
    private String installationCancelledByErrorMessage2 = "code: ";
    private String installationCancelledByErrorMessage3
            = "visit https://github.com/benchdoos/WeblocOpener for more info.";

    public UpdateDialog() {
        serverAppVersion = new AppVersion();

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        setIconImage(Toolkit.getDefaultToolkit().getImage(UpdateDialog.class.getResource("/icon.png")));

        buttonOK.addActionListener(e -> {
            updateThread = new Thread(() -> onOK());
            updateThread.start();
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                           JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
        setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
        setSize(new Dimension(400, 170));
        setResizable(false);
        loadProperties();
        translateDialog();
    }

    private void translateDialog() {
        translation = new Translation("translations/UpdateDialogBundle") {
            @Override
            public void initTranslations() {
                setTitle(messages.getString("windowTitle"));
                buttonOK.setText(messages.getString("buttonOk"));
                buttonCancel.setText(messages.getString("buttonCancel"));

                currentVersionStringLabel.setText(messages.getString("currentVersionStringLabel"));
                avaliableVersionStringLabel.setText(messages.getString("avaliableVersionStringLabel"));
                availableVersionLabel.setText(messages.getString("availableVersionLabel"));

                successTitle = messages.getString("successTitle");
                successUpdatedMessage = messages.getString("successUpdatedMessage");
                installationCancelledTitle = messages.getString("installationCancelledTitle");
                installationCancelledMessage = messages.getString("installationCancelledMessage");
                noPermissionsMessage = messages.getString("noPermissionsMessage");
                installationCancelledByErrorMessage1 = messages.getString("installationCancelledByErrorMessage1");
                installationCancelledByErrorMessage2 = messages.getString("installationCancelledByErrorMessage2");
                installationCancelledByErrorMessage3 = messages.getString("installationCancelledByErrorMessage3");
            }
        };
        translation.initTranslations();
    }

    public void checkForUpdates() {
        progressBar1.setIndeterminate(true);
        Updater updater = new Updater();
        serverAppVersion = updater.getAppVersion();
        progressBar1.setIndeterminate(false);
        availableVersionLabel.setText(serverAppVersion.getVersion());

        setNewVersionSizeInfo();

        String str;
        try {
            str = RegistryManager.getAppVersionValue();
        } catch (RegistryCanNotReadInfoException e) {
            RegistryManager.setDefaultSettings();
            str = ApplicationConstants.APP_VERSION;
        }
        compareVersions(str);
    }

    private void compareVersions(String str) {
        if (Internal.versionCompare(str, serverAppVersion.getVersion()) < 0) {
            //Need to update
            buttonOK.setEnabled(true);
            buttonOK.setText(translation.messages.getString("buttonOk"));
        } else if (Internal.versionCompare(str, serverAppVersion.getVersion()) > 0) {
            //App version is bigger then on server
            buttonOK.setText(translation.messages.getString("buttonOkDev"));
//            buttonOK.setEnabled(true);
            buttonOK.setEnabled(false); //TODO TURN BACK BEFORE RELEASE
        } else if (Internal.versionCompare(str, serverAppVersion.getVersion()) == 0) {
            //No reason to update;
            buttonOK.setText(translation.messages.getString("buttonOkUp2Date"));
        }
    }

    private void setNewVersionSizeInfo() {
        if (serverAppVersion.getSize() > 1024 * 1024) {
            double size = serverAppVersion.getSize() / ((double) 1024 / (double) 1024);
            size = size * 100;
            int i = (int) Math.round(size);
            size = (double) i / 100;
            newVersionSizeLabel.setText(Double.toString(size));
            unitLabel.setText("MB");
        } else {
            newVersionSizeLabel.setText(serverAppVersion.getSize() / 1024 + "");
            unitLabel.setText("KB");
        }
    }

    private void loadProperties() {

        try {
            SettingsManager.loadInfo();
            currentVersionLabel.setText(RegistryManager.getAppVersionValue());
        } catch (RegistryException e) {
            currentVersionLabel.setText(ApplicationConstants.APP_VERSION);
        }
        availableVersionLabel.setText("Unknown");
    }


    private void onOK() {
        buttonOK.setEnabled(false);
        if (!Thread.currentThread().isInterrupted()) {
            buttonOK.setEnabled(true);
            processUpdateResult(Updater.startUpdate(serverAppVersion));
        } else {
            buttonOK.setEnabled(false);
        }

        //dispose();
    }

    private void processUpdateResult(int successUpdate) {
        switch (successUpdate) {
            case 0: //NORMAL state, app updated
                updateSuccessfullyInstalled();

                break;
            case 1: //Installation was cancelled or Incorrect function or corrupt file
                showErrorMessageToUser(this, installationCancelledTitle,
                                       installationCancelledMessage);
                Updater.installerFile.delete();
                break;
            case 2: //The system cannot find the file specified. OR! User gave no permissions.
                showErrorMessageToUser(this, installationCancelledTitle, noPermissionsMessage);
                break;

            case 193: //Installation file is corrupt
                showErrorMessageToUser(this, installationCancelledTitle, noPermissionsMessage);
                break;
            default:
                String message = installationCancelledByErrorMessage1
                        + "\n" + installationCancelledByErrorMessage2 +
                        successUpdate
                        + "\n" + installationCancelledByErrorMessage3;
                showErrorMessageToUser(this, installationCancelledTitle, message);

                break;
        }
    }

    private void updateSuccessfullyInstalled() {
        try {
            SettingsManager.loadInfo();
        } catch (RegistryCanNotReadInfoException | RegistryCanNotWriteInfoException ignore) {
            /*NOP*/
        }


        JOptionPane.showMessageDialog(this, successUpdatedMessage
                + serverAppVersion.getVersion(), successTitle, JOptionPane.INFORMATION_MESSAGE);

        if (Main.mode != Main.Mode.AFTER_UPDATE) {
            try {
                dispose();
                String value = RegistryManager.getInstallLocationValue();
                final String command
                        = "java -jar \"" + value + "Updater.jar\" " + ApplicationConstants.UPDATE_DELETE_TEMP_FILE_ARGUMENT;
                System.out.println(
                        "running " + ApplicationConstants.UPDATE_DELETE_TEMP_FILE_ARGUMENT + " " +
                                "argument: " + command);
                Runtime.getRuntime().exec(command);
                System.exit(0);
            } catch (RegistryCanNotReadInfoException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void onCancel() {
        // add your code here if necessary
        if (updateThread != null) {
            if (!updateThread.isInterrupted()) {
                updateThread.interrupt();
                System.out.println("Installation was interrupted: " + updateThread.isInterrupted());
                new File(ApplicationConstants.UPDATE_PATH_FILE + "WeblocOpenerSetup"
                                 + serverAppVersion.getVersion() + "" + ".exe").delete();
            }
        }
        //TODO any fix???
        File updateJar = new File(ApplicationConstants.UPDATE_PATH_FILE + "Updater_.jar");
        if (updateJar.exists()) {
            try {
                Runtime.getRuntime().exec("java -jar \""
                                                  + RegistryManager.getInstallLocationValue()
                                                  + "\"Updater.jar " + ApplicationConstants.UPDATE_DELETE_TEMP_FILE_ARGUMENT);
            } catch (IOException | RegistryCanNotReadInfoException ignore) {/*NOP*/}
        }
        dispose();
    }

    public AppVersion getAppVersion() {
        return serverAppVersion;
    }

}

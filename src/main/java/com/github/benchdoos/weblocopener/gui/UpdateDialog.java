/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.gui;

import com.github.benchdoos.weblocopener.service.UpdateInfoExtractor;
import com.github.benchdoos.weblocopener.service.impl.DefaultUpdateInfoExtractor;
import com.github.benchdoos.weblocopener.service.impl.DefaultUpdateService;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.utils.FrameUtils;
import com.github.benchdoos.weblocopener.utils.UpdateHelperUtil;
import com.github.benchdoos.weblocopenercore.constants.StringConstants;
import com.github.benchdoos.weblocopenercore.domain.preferences.DevModeFeatureType;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion.Asset;
import com.github.benchdoos.weblocopenercore.domain.version.Beta;
import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;
import com.github.benchdoos.weblocopenercore.exceptions.NoAvailableVersionException;
import com.github.benchdoos.weblocopenercore.gui.Translatable;
import com.github.benchdoos.weblocopenercore.gui.elements.ImagePanel;
import com.github.benchdoos.weblocopenercore.service.UrlsProceed;
import com.github.benchdoos.weblocopenercore.service.WindowLauncher;
import com.github.benchdoos.weblocopenercore.service.notification.NotificationManager;
import com.github.benchdoos.weblocopenercore.service.settings.dev_mode.DevModeFeatureCheck;
import com.github.benchdoos.weblocopenercore.service.settings.impl.DarkModeActiveSettings;
import com.github.benchdoos.weblocopenercore.service.translation.Translation;
import com.github.benchdoos.weblocopenercore.utils.CoreUtils;
import com.github.benchdoos.weblocopenercore.utils.VersionUtils;
import com.github.benchdoos.weblocopenercore.utils.system.OS;
import com.github.benchdoos.weblocopenercore.utils.version.Version;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.log4j.Log4j2;
import org.bridj.Pointer;
import org.bridj.PointerIO;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.shell.ITaskbarList3;
import org.bridj.jawt.JAWTUtils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"ALL", "ResultOfMethodCallIgnored"})
@Log4j2
public class UpdateDialog extends JFrame implements Translatable {

  public static final String UPDATE_DIALOG_BUNDLE = "UpdateDialogBundle";
  private JProgressBar progressBar;
  private JButton buttonOK;
  private JButton buttonCancel;
  private Updater updater;
  private AppVersion serverAppVersion;
  private JPanel contentPane;
  private JLabel currentVersionLabel;
  private JLabel availableVersionLabel;
  private JLabel newVersionSizeLabel;
  private JLabel unitLabel;
  private JLabel currentVersionStringLabel;
  private JLabel availableVersionStringLabel;
  private JButton updateInfoButton;
  private Thread updateThread = null;
  private JButton manualDownloadButton;
  private JLabel serverBetaLabel;
  private JLabel currentBetaLabel;
  private JPanel imagePanel;
  private ITaskbarList3 taskBar = null;
  private UpdateInfo updateInfo = null;
  private UpdateInfoExtractor updateInfoExtractor;

  public UpdateDialog() {
    $$$setupUI$$$();
    updateInfoExtractor = new DefaultUpdateInfoExtractor();
    updater = UpdateHelperUtil.getUpdaterForCurrentOS();
    iniGui();
    loadProperties();
  }

  public void checkForUpdates() {
    progressBar.setIndeterminate(true);
    log.debug("Provided updater: {}", updater);
    if (updater != null) {
      createDefaultActionListeners();

      serverAppVersion = new DefaultUpdateService(updater).getLatest();

      initBetaLabel();

      progressBar.setIndeterminate(false);

      final Version version = serverAppVersion.version();
      availableVersionLabel.setText(version.getSimpleVersionWithoutBeta());
      setNewVersionSizeInfo();

      CompletableFuture.runAsync(this::getUpdateInfo);

      compareVersions();
    } else {
      removeAllListeners(buttonOK);

      progressBar.setIndeterminate(false);
      buttonOK.setEnabled(true);
      buttonOK.setText(Translation.get(UPDATE_DIALOG_BUNDLE, "retryButton"));
      buttonOK.addActionListener(
              e1 -> {
                progressBar.setIndeterminate(true);
                checkForUpdates();
              });
    }
  }

  public JButton getButtonCancel() {
    return buttonCancel;
  }

  public JProgressBar getProgressBar() {
    return progressBar;
  }

  @Override
  public void translate() {
    Translation translation = new Translation(UPDATE_DIALOG_BUNDLE);
    currentVersionStringLabel.setText(translation.get("currentVersionStringLabel"));
    availableVersionStringLabel.setText(translation.get("availableVersionStringLabel"));
    updateInfoButton.setToolTipText(translation.get("infoAboutUpdate"));
    buttonOK.setText(translation.get("buttonOk"));
    manualDownloadButton.setText(translation.get("manualDownloadButtonText"));
    manualDownloadButton.setToolTipText(translation.get("manualDownloadButtonToolTip"));
    buttonCancel.setText(translation.get("buttonCancel"));
  }

  private void prepareWindowsTaskBarHandling() {
    try {
      if (OS.isWindows()) {
        log.debug("Initing task bar handling for Windows OS");
        taskBar = COMRuntime.newInstance(ITaskbarList3.class);
      }
    } catch (ClassNotFoundException ignore) {
      /*WINDOWS<WINDOWS 7*/
    }
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout(0, 0));
    imagePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    contentPane.add(imagePanel, BorderLayout.CENTER);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 10, 10, 10), -1, -1));
    panel1.setOpaque(false);
    imagePanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel2.setOpaque(false);
    panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    buttonOK = new JButton();
    buttonOK.setActionCommand(this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle_en_EN", "buttonOk"));
    buttonOK.setEnabled(false);
    Font buttonOKFont = this.$$$getFont$$$(null, Font.BOLD, -1, buttonOK.getFont());
    if (buttonOKFont != null) buttonOK.setFont(buttonOKFont);
    this.$$$loadButtonText$$$(buttonOK, this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle", "buttonOk"));
    panel2.add(buttonOK, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    buttonCancel = new JButton();
    buttonCancel.setActionCommand(this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle_en_EN", "buttonCancel"));
    this.$$$loadButtonText$$$(buttonCancel, this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle", "buttonCancel"));
    panel2.add(buttonCancel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    manualDownloadButton = new JButton();
    manualDownloadButton.setIcon(new ImageIcon(getClass().getResource("/images/downloadsIcon16.png")));
    manualDownloadButton.setInheritsPopupMenu(false);
    manualDownloadButton.setMargin(new Insets(2, 2, 2, 8));
    manualDownloadButton.setOpaque(true);
    manualDownloadButton.setRequestFocusEnabled(false);
    this.$$$loadButtonText$$$(manualDownloadButton, this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle", "manualDownloadButtonText"));
    manualDownloadButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle", "manualDownloadButtonToolTip"));
    panel2.add(manualDownloadButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 0, 10), -1, -1));
    panel3.setOpaque(false);
    imagePanel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel3.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
    panel4.setOpaque(false);
    panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    progressBar = new JProgressBar();
    progressBar.setStringPainted(false);
    panel4.add(progressBar, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    currentVersionStringLabel = new JLabel();
    currentVersionStringLabel.setForeground(new Color(-460552));
    this.$$$loadLabelText$$$(currentVersionStringLabel, this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle", "currentVersionStringLabel"));
    panel4.add(currentVersionStringLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    availableVersionStringLabel = new JLabel();
    availableVersionStringLabel.setForeground(new Color(-460552));
    this.$$$loadLabelText$$$(availableVersionStringLabel, this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle", "availableVersionStringLabel"));
    panel4.add(availableVersionStringLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel5.setOpaque(false);
    panel4.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    currentVersionLabel = new JLabel();
    Font currentVersionLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, currentVersionLabel.getFont());
    if (currentVersionLabelFont != null) currentVersionLabel.setFont(currentVersionLabelFont);
    currentVersionLabel.setForeground(new Color(-460552));
    currentVersionLabel.setText("1.0.0");
    panel5.add(currentVersionLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    currentBetaLabel = new JLabel();
    currentBetaLabel.setForeground(new Color(-460552));
    currentBetaLabel.setText("(beta)");
    panel5.add(currentBetaLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer3 = new Spacer();
    panel5.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel6.setOpaque(false);
    panel4.add(panel6, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final Spacer spacer4 = new Spacer();
    panel6.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    updateInfoButton = new JButton();
    updateInfoButton.setBorderPainted(false);
    updateInfoButton.setContentAreaFilled(false);
    updateInfoButton.setDefaultCapable(true);
    updateInfoButton.setDoubleBuffered(false);
    updateInfoButton.setEnabled(false);
    updateInfoButton.setIcon(new ImageIcon(getClass().getResource("/images/infoIcon16.png")));
    updateInfoButton.setMargin(new Insets(2, 2, 2, 2));
    updateInfoButton.setOpaque(false);
    updateInfoButton.setRequestFocusEnabled(false);
    updateInfoButton.setText("");
    updateInfoButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/UpdateDialogBundle", "infoAboutUpdate"));
    panel6.add(updateInfoButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(18, 18), new Dimension(18, 18), new Dimension(18, 18), 0, false));
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
    panel7.setOpaque(false);
    panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    availableVersionLabel = new JLabel();
    Font availableVersionLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, availableVersionLabel.getFont());
    if (availableVersionLabelFont != null) availableVersionLabel.setFont(availableVersionLabelFont);
    availableVersionLabel.setForeground(new Color(-460552));
    availableVersionLabel.setText("1.0.0");
    panel7.add(availableVersionLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer5 = new Spacer();
    panel7.add(spacer5, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    serverBetaLabel = new JLabel();
    serverBetaLabel.setForeground(new Color(-460552));
    serverBetaLabel.setText("(beta)");
    panel7.add(serverBetaLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    newVersionSizeLabel = new JLabel();
    newVersionSizeLabel.setForeground(new Color(-460552));
    newVersionSizeLabel.setText("10");
    panel7.add(newVersionSizeLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    unitLabel = new JLabel();
    unitLabel.setForeground(new Color(-460552));
    unitLabel.setText("MB");
    panel7.add(unitLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
  }

  private static Method $$$cachedGetBundleMethod$$$ = null;

  private String $$$getMessageFromBundle$$$(String path, String key) {
    ResourceBundle bundle;
    try {
      Class<?> thisClass = this.getClass();
      if ($$$cachedGetBundleMethod$$$ == null) {
        Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
        $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
      }
      bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
    } catch (Exception e) {
      bundle = ResourceBundle.getBundle(path);
    }
    return bundle.getString(key);
  }

  /**
   * @noinspection ALL
   */
  private void $$$loadLabelText$$$(JLabel component, String text) {
    StringBuffer result = new StringBuffer();
    boolean haveMnemonic = false;
    char mnemonic = '\0';
    int mnemonicIndex = -1;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '&') {
        i++;
        if (i == text.length()) break;
        if (!haveMnemonic && text.charAt(i) != '&') {
          haveMnemonic = true;
          mnemonic = text.charAt(i);
          mnemonicIndex = result.length();
        }
      }
      result.append(text.charAt(i));
    }
    component.setText(result.toString());
    if (haveMnemonic) {
      component.setDisplayedMnemonic(mnemonic);
      component.setDisplayedMnemonicIndex(mnemonicIndex);
    }
  }

  /**
   * @noinspection ALL
   */
  private void $$$loadButtonText$$$(AbstractButton component, String text) {
    StringBuffer result = new StringBuffer();
    boolean haveMnemonic = false;
    char mnemonic = '\0';
    int mnemonicIndex = -1;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '&') {
        i++;
        if (i == text.length()) break;
        if (!haveMnemonic && text.charAt(i) != '&') {
          haveMnemonic = true;
          mnemonic = text.charAt(i);
          mnemonicIndex = result.length();
        }
      }
      result.append(text.charAt(i));
    }
    component.setText(result.toString());
    if (haveMnemonic) {
      component.setMnemonic(mnemonic);
      component.setDisplayedMnemonicIndex(mnemonicIndex);
    }
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return contentPane;
  }

  private void getUpdateInfo() {
    if (serverAppVersion != null) {
      final UpdateInfo extract = updateInfoExtractor.extract(serverAppVersion);
      if (extract != null) {
        this.updateInfo = extract;
      }
      updateInfoButton.setEnabled(true);
    }
  }

  private void initBetaLabel() {
    final Beta serverAppVersionBeta = serverAppVersion.getBeta();
    serverBetaLabel.setVisible(serverAppVersionBeta.isBeta());
    serverBetaLabel.setText(serverAppVersionBeta.getBeautifulBetaString());
  }

  private void compareVersions() {
    switch (new VersionUtils().versionCompare(serverAppVersion, new CoreUtils().getCurrentAppVersion())
            .getVersionCompare()) {
      case FIRST_VERSION_IS_NEWER:
        buttonOK.setEnabled(true);
        buttonOK.setText(Translation.get(UPDATE_DIALOG_BUNDLE, "buttonOk"));
        break;
      case SECOND_VERSION_IS_NEWER:
        if (new DevModeFeatureCheck().isActive(DevModeFeatureType.UI_UPDATER_ENABLE_DEV_OPTIONS)) {
          buttonOK.setText(Translation.get(UPDATE_DIALOG_BUNDLE, "buttonOkDev"));
          buttonOK.setEnabled(true);
        } else {
          buttonOK.setText(Translation.get(UPDATE_DIALOG_BUNDLE, "buttonOkUp2Date"));
        }
        break;
      case VERSIONS_ARE_EQUAL:
        buttonOK.setText(Translation.get(UPDATE_DIALOG_BUNDLE, "buttonOkUp2Date"));
        break;
    }
  }

  private void createDefaultActionListeners() {
    for (ActionListener actionListener : buttonOK.getActionListeners()) {
      buttonOK.removeActionListener(actionListener);
    }

    for (ActionListener actionListener : buttonCancel.getActionListeners()) {
      buttonCancel.removeActionListener(actionListener);
    }

    buttonOK.addActionListener(e -> {
      updateThread = new Thread(this::onOK);
      updateThread.start();
    });

    buttonCancel.addActionListener(e -> onCancel());
  }

  private void iniGui() {
    setTitle(Translation.get(UPDATE_DIALOG_BUNDLE, "windowTitle"));
    setContentPane(contentPane);
    getRootPane().setDefaultButton(buttonOK);
    setIconImage(Toolkit.getDefaultToolkit()
            .getImage(UpdateDialog.class.getResource("/images/updateIconBlue256.png")));

    serverBetaLabel.setVisible(false);

    createDefaultActionListeners();

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    contentPane.registerKeyboardAction(
            e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    initListeners();

    initWindowListeners();

    prepareWindowsTaskBarHandling();

    translate();

    pack();
    setLocation(FrameUtils.getFrameOnCenterLocationPoint(this));
    setSize(new Dimension(400, 170));
    setResizable(false);
  }

  private void initWindowListeners() {
    addWindowListener(
            new WindowAdapter() {
              @Override
              public void windowClosing(WindowEvent e) {
                onCancel();
              }
            });
  }

  private void initListeners() {
    initUpdateInfoButton();

    manualDownloadButton.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                openSetupUrl();
              }

              private void openSetupUrl() {
                log.debug("Calling to download setup manually");
                URL url = null;
                if (updater != null) {

                  if (serverAppVersion != null) {
                    try {
                      final Asset installerAsset = updater.getInstallerAsset(serverAppVersion);
                      log.debug("Trying to open [" + installerAsset + "]");
                      url = installerAsset.downloadUrl();
                      new UrlsProceed().openUrl(url);
                    } catch (final NoAvailableVersionException e) {
                      log.warn("Could not get asset url, opening github page: {}",
                              serverAppVersion.gitHubReleasePageUrl());
                      new UrlsProceed().openUrl(serverAppVersion.gitHubReleasePageUrl());
                    }

                  } else {
                    new UrlsProceed().openUrl(StringConstants.UPDATE_WEB_URL);
                  }

                } else {
                  new UrlsProceed().openUrl(StringConstants.UPDATE_WEB_URL);
                }
              }
            });
  }

  private void initUpdateInfoButton() {
    updateInfoButton.addActionListener(e -> onUpdateInfoButton());

    updateInfoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  private void loadProperties() {
    clearLabels();
    initCurrentVersionInfo();

    availableVersionLabel.setText(
            Translation.get(UPDATE_DIALOG_BUNDLE, "availableVersionLabelUnknown"));
  }

  private void clearLabels() {
    availableVersionLabel.setText("");
    serverBetaLabel.setText("");
    newVersionSizeLabel.setText("");
    unitLabel.setText("");
  }

  private void initCurrentVersionInfo() {
    final AppVersion currentApplicationVersion = new CoreUtils().getCurrentAppVersion();

    currentVersionLabel.setText(currentApplicationVersion.version().getSimpleVersionWithoutBeta());

    currentBetaLabel.setVisible(currentApplicationVersion.getBeta().isBeta());
    final Beta beta = currentApplicationVersion.getBeta();

    currentBetaLabel.setText(beta.getBeautifulBetaString());
  }

  private void onCancel() {
    if (updateThread != null) {
      if (!updateThread.isInterrupted()) {
        updateThread.interrupt();
        log.info("Installation was interrupted: " + updateThread.isInterrupted());
        buttonOK.setEnabled(true);
        progressBar.setValue(0);
        runCleanInstallerFile();
      } else {
        runCleanInstallerFile();
        dispose();
      }
    } else {
      runCleanInstallerFile();
      dispose();
    }
  }

  private void onOK() {
    buttonOK.setEnabled(false);

    final boolean windows = OS.isWindows();
    final com.github.benchdoos.weblocopenercore.service.actions.ActionListener<Object> listener =
            i -> {
              progressBar.setValue((int) i);
              if (windows) {
                updateWindowsProgressBar((int) i);
              }
            };

    if (!Thread.currentThread().isInterrupted()) {
      try {
        updater.addListener(listener);
        progressBar.setStringPainted(true);
        updater.startUpdate(serverAppVersion);
      } catch (IOException | NoAvailableVersionException e) {
        try {
          final Asset installerAsset = updater.getInstallerAsset(serverAppVersion);
          if (installerAsset.downloadUrl() != null) {
            log.warn("Could not start update", e);

            Translation translation = new Translation(UPDATE_DIALOG_BUNDLE);

            NotificationManager.getForcedNotification(this)
                    .showErrorNotification(
                            translation.get("unableToUpdateTitle"),
                            translation.get("lostConnectionMessage"));
          }
        } catch (NoAvailableVersionException ex) {
          log.warn("Could not start update, there is no available version for this system", e);
          Translation translation = new Translation(UPDATE_DIALOG_BUNDLE);
          NotificationManager.getForcedNotification(this)
                  .showErrorNotification(
                          translation.get("unableToUpdateTitle"), translation.get("noAvailableVersion"));
        }
      }
      if (!Thread.currentThread().isInterrupted()) {
        dispose();
      }
    } else {
      updater.removeListener(listener);
      buttonOK.setEnabled(true);
      buttonCancel.setEnabled(true);
    }
  }

  private void updateWindowsProgressBar(final int i) {
    if (taskBar != null) {
      long nativePeerHandle = JAWTUtils.getNativePeerHandle(this);
      final Pointer<?> pointer =
              Pointer.pointerToAddress(
                      nativePeerHandle, PointerIO.getSizeTInstance().getTargetSize(), null);
      taskBar.SetProgressValue((Pointer<Integer>) pointer, i, 100);
    }
  }

  private void onUpdateInfoButton() {
    if (serverAppVersion != null) {
      if (!serverAppVersion.releaseInfo().isEmpty()) {
        new WindowLauncher<UpdateInfoDialog>() {
          @Override
          public UpdateInfoDialog initWindow() {
            return new UpdateInfoDialog(serverAppVersion, updateInfo);
          }
        }.getWindow().setVisible(true);
      }
    }
  }

  private void removeAllListeners(JButton button) {
    for (ActionListener al : button.getActionListeners()) {
      button.removeActionListener(al);
    }
  }

  private void runCleanInstallerFile() {
    if (updater != null && updater.getInstallerFile() != null && serverAppVersion != null) {
      final File installerFile = updater.getInstallerFile();
      log.info("Marking to delete on app exit installer file: " + installerFile);
      installerFile.deleteOnExit();
    } else {
      log.debug("No file to cleanup, serverAppVersion is null");
    }
  }

  private void setNewVersionSizeInfo() {
    try {
      final Asset installerAsset = updater.getInstallerAsset(serverAppVersion);
      if (installerAsset.size() > 1024 * 1024) {
        double size = installerAsset.size() / 1024 / (double) 1024;
        size = size * 100;
        int i = (int) Math.round(size);
        size = (double) i / 100;
        newVersionSizeLabel.setText(Double.toString(size));
        unitLabel.setText("MB");
      } else {
        newVersionSizeLabel.setText(installerAsset.size() / 1024 + "");
        unitLabel.setText("KB");
      }
    } catch (final NoAvailableVersionException e) {
      log.warn("Can not get available asset for size info", e);
    }
  }

  private void createUIComponents() {
    ImageIcon icon;
    if (Boolean.FALSE.equals(new DarkModeActiveSettings().getValue())) {
      icon = new ImageIcon(getClass().getResource("/images/updaterBackground.png"));
    } else {
      icon = new ImageIcon(getClass().getResource("/images/updaterBackgroundDark.png"));
    }
    imagePanel = new ImagePanel(icon);
  }
}

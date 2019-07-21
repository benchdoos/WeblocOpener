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

package com.github.benchdoos.weblocopener.update;


import com.github.benchdoos.weblocopener.core.constants.ArgumentConstants;
import com.github.benchdoos.weblocopener.core.constants.PathConstants;
import com.github.benchdoos.weblocopener.gui.UpdateDialog;
import com.github.benchdoos.weblocopener.utils.version.ApplicationVersion;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.bridj.Pointer;
import org.bridj.PointerIO;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.shell.ITaskbarList3;
import org.bridj.jawt.JAWTUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Log4j2
public class WindowsUpdater implements Updater {
    private static ApplicationVersion latestReleaseVersion = null;
    private static ApplicationVersion latestBetaVersion = null;

    private static void update(File file) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(file.getAbsolutePath() + ArgumentConstants.INSTALLER_SILENT_KEY);
        System.exit(0);
    }

    @Override
    public ApplicationVersion getLatestAppVersion() {
        return UpdaterManager.getLatestVersion(this);
    }

    @Override
    public ApplicationVersion getLatestReleaseAppVersion() {
        if (latestReleaseVersion != null) return latestReleaseVersion;
        return latestReleaseVersion = UpdaterManager.getLatestReleaseVersion(Updater.WINDOWS_SETUP_DEFAULT_NAME);
    }

    @Override
    public ApplicationVersion getLatestBetaAppVersion() {
        if (latestBetaVersion != null) return latestBetaVersion;

        return latestBetaVersion = UpdaterManager.getLatestBetaVersion(Updater.WINDOWS_SETUP_DEFAULT_NAME);
    }

    @Override
    public void startUpdate(ApplicationVersion applicationVersion) throws IOException {
        log.info("Starting update for {}", applicationVersion.getVersion());
        File installerFile = new File(
                PathConstants.UPDATE_PATH_FILE + DEBIAN_SETUP_DEFAULT_NAME);
        updateProgressBar(applicationVersion, installerFile);

        try {
            FileUtils.copyURLToFile(new URL(applicationVersion.getDownloadUrl()), installerFile, Updater.CONNECTION_TIMEOUT, Updater.CONNECTION_TIMEOUT);

            update(installerFile);
        } catch (IOException e) {
            log.warn("Can not download file: {} to {}", applicationVersion.getDownloadUrl(), installerFile, e);
            installerFile.deleteOnExit();
            throw new IOException(e);
        }
    }

    private void updateProgressBar(ApplicationVersion applicationVersion, File file) {
        if (UpdateDialog.getInstance() != null) {
            JProgressBar progressBar = UpdateDialog.getInstance().getProgressBar();
            ITaskbarList3 taskBar = null;
            Pointer<?> pointer;

            final long size = applicationVersion.getSize();
            progressBar.setMaximum(Math.toIntExact(size));
            progressBar.setStringPainted(true);


            try {
                taskBar = COMRuntime.newInstance(ITaskbarList3.class);
            } catch (ClassNotFoundException ignore) {/*WINDOWS<WINDOWS 7*/}
            long nativePeerHandle = JAWTUtils.getNativePeerHandle(UpdateDialog.getInstance());
            pointer = Pointer.pointerToAddress(nativePeerHandle, PointerIO.getSizeTInstance().getTargetSize(), null);


            Timer timer = new Timer(500, null);
            ITaskbarList3 finalTaskBar = taskBar;
            final ActionListener actionListener = e -> {
                progressBar.setValue(Math.toIntExact(file.length()));
                if (finalTaskBar != null) {
                    finalTaskBar.SetProgressValue((Pointer<Integer>) pointer, progressBar.getValue(),
                            progressBar.getMaximum());
                }
                if (file.length() == applicationVersion.getSize()) {
                    timer.stop();
                }
            };
            timer.addActionListener(actionListener);
            timer.setRepeats(true);
            timer.start();
        }
    }
}
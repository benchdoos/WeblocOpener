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

package com.github.benchdoos.weblocopener.update.impl;

import com.github.benchdoos.weblocopener.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.gui.UpdateDialog;
import com.github.benchdoos.weblocopener.update.Updater;
import com.github.benchdoos.weblocopener.update.UpdaterHelper;
import com.github.benchdoos.weblocopenercore.domain.version.ApplicationVersion;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Files;

import javax.swing.JProgressBar;
import javax.swing.Timer;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class UnixUpdater implements Updater {
    private static AtomicReference<ApplicationVersion> latestReleaseVersion = null;
    private static AtomicReference<ApplicationVersion> latestBetaVersion = null;

    private static final Object RELEASE_MUTEX = new Object();
    private static final Object BETA_MUTEX = new Object();

    private final UpdaterHelper updaterHelper;

    public UnixUpdater() {
        updaterHelper = new UpdaterHelper();
    }

    @Override
    public ApplicationVersion getLatestAppVersion() {
        return updaterHelper.getLatestVersion(this);
    }

    @Override
    public ApplicationVersion getLatestReleaseAppVersion() {
        if (latestReleaseVersion != null) {
            return latestReleaseVersion.get();
        }

        synchronized (RELEASE_MUTEX) {
            final ApplicationVersion version =
                updaterHelper.getLatestReleaseVersion(ApplicationConstants.DEBIAN_SETUP_DEFAULT_NAME);
            latestReleaseVersion = new AtomicReference<>(version);

            return latestReleaseVersion.get();
        }
    }

    @Override
    public ApplicationVersion getLatestBetaAppVersion() {
        if (latestBetaVersion != null) {
            return latestBetaVersion.get();
        }

        synchronized (BETA_MUTEX) {
            final ApplicationVersion version =
                updaterHelper.getLatestBetaVersion(ApplicationConstants.DEBIAN_SETUP_DEFAULT_NAME);
            latestBetaVersion = new AtomicReference<>(version);

            return latestBetaVersion.get();
        }
    }

    @Override
    public void startUpdate(ApplicationVersion applicationVersion) throws IOException {
        log.info("Starting update for {}", applicationVersion.getVersion());
        File installerFile = new File(
                ApplicationConstants.UPDATE_PATH_FILE + ApplicationConstants.DEBIAN_SETUP_DEFAULT_NAME);
        if (!installerFile.exists()) {
            updateAndInstall(applicationVersion, installerFile);
        } else {
            if (applicationVersion.getSize() == installerFile.length()) {
                updateProgressBar(applicationVersion, installerFile);
                update(installerFile);
            } else {
                Files.delete(installerFile);
                updateAndInstall(applicationVersion, installerFile);
            }
        }
    }

    private void updateAndInstall(ApplicationVersion applicationVersion, File installerFile) throws IOException {
        updateProgressBar(applicationVersion, installerFile);

        try {
            FileUtils.copyURLToFile(new URL(applicationVersion.getDownloadUrl()), installerFile, ApplicationConstants.CONNECTION_TIMEOUT, ApplicationConstants.CONNECTION_TIMEOUT);

            update(installerFile);
        } catch (IOException e) {
            log.warn("Can not download file: {} to {}", applicationVersion.getDownloadUrl(), installerFile, e);
            installerFile.deleteOnExit();
            throw new IOException(e);
        }
    }

    private void update(File installerFile) throws IOException {
        Desktop.getDesktop().open(installerFile);
        System.exit(0);
    }


    private void updateProgressBar(ApplicationVersion applicationVersion, File file) {
        if (UpdateDialog.getInstance() != null) {
            JProgressBar progressBar = UpdateDialog.getInstance().getProgressBar();

            final long size = applicationVersion.getSize();
            progressBar.setMaximum(Math.toIntExact(size));


            Timer timer = new Timer(500, null);

            final ActionListener actionListener = e -> {
                progressBar.setValue(Math.toIntExact(file.length()));
                if (file.length() == applicationVersion.getSize()) {
                    timer.stop();
                }
            };
            timer.addActionListener(actionListener);
            timer.setRepeats(true);
            timer.start();
        }
    }

    @Override
    public String toString() {
        return "UnixUpdater [" +
                "installerFile = " + ApplicationConstants.DEBIAN_SETUP_DEFAULT_NAME +
                "]";
    }


}

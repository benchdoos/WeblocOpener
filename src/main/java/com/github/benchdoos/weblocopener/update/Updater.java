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


import com.github.benchdoos.weblocopener.core.Application;
import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.ArgumentConstants;
import com.github.benchdoos.weblocopener.core.constants.PathConstants;
import com.github.benchdoos.weblocopener.gui.UpdateDialog;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.UserUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bridj.Pointer;
import org.bridj.PointerIO;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.shell.ITaskbarList3;
import org.bridj.jawt.JAWTUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.*;
import java.net.URL;

/**
 * Created by Eugene Zrazhevsky on 03.11.2016.
 */
public class Updater {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());


    private static final String GITHUB_URL = "https://api.github.com/repos/benchdoos/WeblocOpener/releases/latest";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String WINDOWS_SETUP_DEFAULT_NAME = "WeblocOpenerSetup.exe";
    private static File installerFile = null;
    private static HttpsURLConnection connection = null;
    private static String canNotUpdateTitle = "Can not Update";
    private static String canNotUpdateMessage = "Can not connect to api.github.com";
    private AppVersion appVersion = null;


    public Updater() throws IOException, NullPointerException {
        translateMessages();
        createConnection();

        getServerApplicationVersion();
    }

    public static void canNotConnectManage(Exception e) {


        log.warn(canNotUpdateMessage, e);
        if (Application.updateMode != Application.UPDATE_MODE.SILENT) {
            UserUtils.showErrorMessageToUser(null, canNotUpdateTitle, canNotUpdateMessage);
        }
    }

    private static void downloadFile(AppVersion appVersion, JProgressBar progressBar,
                                     ITaskbarList3 taskBar, Pointer<?> pointer,
                                     BufferedInputStream bis, FileOutputStream fos) throws IOException {
        try {
            final int bufferLength = 1024 * 1024;
            final byte[] data = new byte[bufferLength];
            int count;
            int progress = 0;
            while ((count = bis.read(data, 0, bufferLength)) != -1) {
                if (!Thread.currentThread().isInterrupted()) {
                    fos.write(data, 0, count);
                    progress += count;

                } else {
                    log.debug("File {} deleted: {}", installerFile, installerFile.delete());
                    progress = 0;
                }

                updateProgressInfo(progressBar, taskBar, pointer, progress);

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            if (installerFile.exists() && installerFile.getName().contains("WeblocOpenerSetup")) {
                log.debug("File {} deleted: {}", installerFile, installerFile.delete());
            }
            log.warn("Could not download file: {} to {}", appVersion.getDownloadUrl(), installerFile, e);
        }
    }

    private static File downloadNewVersionInstaller(AppVersion appVersion) throws IOException {
        JProgressBar progressBar = null;
        if (UpdateDialog.getInstance() != null) {
            progressBar = UpdateDialog.getInstance().getProgressBar();
        }

        ITaskbarList3 taskBar = null;
        Pointer<?> pointer;

        try {
            taskBar = COMRuntime.newInstance(ITaskbarList3.class);
        } catch (ClassNotFoundException ignore) {/*WINDOWS<WINDOWS 7*/}

        long nativePeerHandle = JAWTUtils.getNativePeerHandle(UpdateDialog.getInstance());
        pointer = Pointer.pointerToAddress(nativePeerHandle, PointerIO.getSizeTInstance().getTargetSize(), null);


        if (progressBar != null) {
            progressBar.setStringPainted(true);
            progressBar.setMaximum((int) appVersion.getSize());
        }


        try (BufferedInputStream bis = new BufferedInputStream(new URL(appVersion.getDownloadUrl()).openStream());
             FileOutputStream fos = new FileOutputStream(installerFile)) {
            downloadFile(appVersion, progressBar, taskBar, pointer, bis, fos);
        } finally {
            if (taskBar != null) {
                taskBar.Release();
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            log.debug("File {} deleted: {}", installerFile, installerFile.delete());
            installerFile.deleteOnExit();
        }

        return installerFile;
    }

    private static void reDownloadOnCorrupt(AppVersion appVersion) throws IOException {
        if (!installerFile.exists() || installerFile.length() != appVersion.getSize()) {

            log.debug("File {} deleted: {}", installerFile, installerFile.delete());

            installerFile = downloadNewVersionInstaller(appVersion);
        }
    }

    public static void startUpdate(AppVersion appVersion) throws IOException {
        log.info("Starting update to " + appVersion.getVersion());
        installerFile = new File(
                PathConstants.UPDATE_PATH_FILE + "WeblocOpenerSetupV" + appVersion.getVersion() + ".exe");
        if (!Thread.currentThread().isInterrupted()) {
            reDownloadOnCorrupt(appVersion);

            if (!Thread.currentThread().isInterrupted()) {

                try {
                    if (!Thread.currentThread().isInterrupted()) {
                        if (installerFile.length() == appVersion.getSize()) {
                            update(installerFile);
                        }
                    }
                } catch (IOException e) {
                    if (e.getMessage().contains("CreateProcess error=193")) {
                        try {
                            if (!Thread.currentThread().isInterrupted()) {
                                log.debug("File {} deleted: {}", installerFile, installerFile.delete());
                                installerFile = downloadNewVersionInstaller(appVersion);
                                update(installerFile);
                            }
                        } catch (IOException e1) {
                            log.warn("Could not re-download new version", e1);
                            log.debug("File {} deleted: {}", installerFile, installerFile.delete());

                        }
                    }
                }
                installerFile.deleteOnExit();

            }
        }
    }

    private static void update(File file) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        UpdateDialog.getInstance().getButtonCancel().setEnabled(false);
        runtime.exec(file.getAbsolutePath() + ArgumentConstants.INSTALLER_SILENT_KEY);
    }

    private static void updateProgressInfo(JProgressBar progressBar, ITaskbarList3 taskbar, Pointer hwnd, int progress) {
        if (progressBar != null) {
            progressBar.setValue(progress);
            if (taskbar != null) {
                //noinspection unchecked
                taskbar.SetProgressValue(hwnd, progressBar.getValue(),
                        progressBar.getMaximum());
            }
        }
    }

    private void createConnection() {
        try {
            getConnection();
            if (!connection.getDoOutput()) {
                connection.setDoOutput(true);
            }
            if (!connection.getDoInput()) {
                connection.setDoInput(true);
            }

        } catch (IOException e) {
            log.warn("Could not establish connection", e);
        }
    }

    private void formAppVersionFromJson(JsonObject root) {
        log.debug("Parsing json to app version");
        final String version = "tag_name";
        final String browser_download_url = "browser_download_url";
        final String assets = "assets";
        final String name = "name";
        final String size = "size";
        final String info = "body";

        appVersion.setVersion(root.getAsJsonObject().get(version).getAsString());
        appVersion.setUpdateInfo(root.getAsJsonObject().get(info).getAsString());
        appVersion.setUpdateTitle(root.getAsJsonObject().get(name).getAsString());

        JsonArray asserts = root.getAsJsonArray(assets);
        for (JsonElement assert_ : asserts) {
            JsonObject userObject = assert_.getAsJsonObject();
            if (userObject.get(name).getAsString().equals(WINDOWS_SETUP_DEFAULT_NAME)) {
                appVersion.setDownloadUrl(userObject.get(browser_download_url).getAsString());
                appVersion.setSize(userObject.get(size).getAsLong());
            }
        }
    }

    public AppVersion getAppVersion() {
        return appVersion;
    }

    private void getConnection() throws IOException {
        URL url = new URL(GITHUB_URL);
        log.debug("Creating connection");

        connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(500);
    }

    private void getServerApplicationVersion() throws IOException {
        log.debug("Getting current server application version");
        String input;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODING));

        input = bufferedReader.readLine();

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(input).getAsJsonObject();

        appVersion = new AppVersion();
        formAppVersionFromJson(root);
        log.info("Server application version: {} {} {}", appVersion.getUpdateTitle(), appVersion.getVersion(), appVersion.getDownloadUrl());
    }

    private void translateMessages() {
        Translation translation = new Translation("translations/UpdaterBundle") {
            @Override
            public void initTranslations() {
                canNotUpdateTitle = messages.getString("canNotUpdateTitle");
                canNotUpdateMessage = messages.getString("canNotUpdateMessage");
            }
        };
        translation.initTranslations();
    }
}

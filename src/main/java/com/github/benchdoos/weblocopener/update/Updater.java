/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
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
@SuppressWarnings({"ALL", "ResultOfMethodCallIgnored"})
public class Updater {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());


    private static final String GITHUB_URL = "https://api.github.com/repos/benchdoos/WeblocOpener/releases/latest";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String WINDOWS_SETUP_DEFAULT_NAME = "WeblocOpenerSetup.exe";
    public static File installerFile = null;
    private static HttpsURLConnection connection = null;
    private static Translation translation;
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

    private static File downloadNewVersionInstaller(AppVersion appVersion) throws IOException {
        JProgressBar progressBar = null;
        if (UpdateDialog.updateDialog != null) {
            progressBar = UpdateDialog.updateDialog.progressBar;
        }

        ITaskbarList3 taskbar = null;
        Pointer<?> hwnd;

        try {
            taskbar = COMRuntime.newInstance(ITaskbarList3.class);
        } catch (ClassNotFoundException ignore) {/*WINDOWS<WINDOWS 7*/}

        long hwndVal = JAWTUtils.getNativePeerHandle(UpdateDialog.updateDialog);
        hwnd = Pointer.pointerToAddress(hwndVal, PointerIO.getSizeTInstance().getTargetSize());


        if (progressBar != null) {
            progressBar.setStringPainted(true);
        }


        try (BufferedInputStream in = new BufferedInputStream(new URL(appVersion.getDownloadUrl()).openStream());
             FileOutputStream fout = new FileOutputStream(installerFile) ){
            try {
                final int bufferLength = 1024 * 1024;
                final byte data[] = new byte[bufferLength];
                int count;
                int progress = 0;
                while ((count = in.read(data, 0, bufferLength)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        installerFile.delete();
                        if (progressBar != null) {
                            progressBar.setValue(0);
                            if (taskbar != null) {
                                //noinspection unchecked
                                taskbar.SetProgressValue((Pointer) hwnd, progressBar.getValue(),
                                        progressBar.getMaximum());
                            }
                        }
                        break;
                    } else {
                        fout.write(data, 0, count);
                        progress += count;
                        int prg = (int) (((double) progress / appVersion.getSize()) * 100);

                        if (progressBar != null) {
                            progressBar.setValue(prg);
                            if (taskbar != null) {
                                //noinspection unchecked
                                taskbar.SetProgressValue((Pointer) hwnd, progressBar.getValue(),
                                        progressBar.getMaximum());
                            }
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                if (installerFile.exists() && installerFile.getName().contains("WeblocOpenerSetup")) {
                    installerFile.delete();
                    /*fout = new FileOutputStream(installerFile);*/
                }
                log.warn("Could not download file: {} to {}", appVersion.getDownloadUrl(), installerFile, e);
            }
        } finally {
            if (taskbar != null) {
                taskbar.Release();
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            installerFile.delete();
            installerFile.deleteOnExit();
        }

        return installerFile;
    }

    private static boolean redownloadOnCorrupt(AppVersion appVersion) throws IOException {
        if (!installerFile.exists() || installerFile.length() != appVersion.getSize()) {

            installerFile.delete();
            installerFile = downloadNewVersionInstaller(appVersion);
            return true;
        }
        return false;
    }

    public static void startUpdate(AppVersion appVersion) throws IOException {
        log.info("Starting update to " + appVersion.getVersion());
        installerFile = new File(
                PathConstants.UPDATE_PATH_FILE + "WeblocOpenerSetupV" + appVersion.getVersion() + ".exe");
        if (!Thread.currentThread().isInterrupted()) {
            redownloadOnCorrupt(appVersion);

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
                                installerFile.delete();
                                installerFile = downloadNewVersionInstaller(appVersion);
                                update(installerFile);
                            }
                        } catch (IOException e1) {
                            log.warn("Could not redownload new version", e1);
                            installerFile.delete();
                        }
                    }
                }
                installerFile.deleteOnExit();

            }
        }
    }

    private static void update(File file) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        UpdateDialog.updateDialog.buttonCancel.setEnabled(false);
        runtime.exec(file.getAbsolutePath() + ArgumentConstants.INSTALLER_SILENT_KEY);
    }

    public void createConnection() {
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

    public void formAppVersionFromJson(JsonObject root) {
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

    private HttpsURLConnection getConnection() throws IOException {
        URL url = new URL(GITHUB_URL);
        log.debug("Creating connection");

        connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(500);
        return connection;
    }

    public void getServerApplicationVersion() throws IOException {
        log.debug("Getting current server application version");
        String input;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODING));

        input = bufferedReader.readLine();

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(input).getAsJsonObject();

        appVersion = new AppVersion();
        formAppVersionFromJson(root);
    }

    private void translateMessages() {
        translation = new Translation("translations/UpdaterBundle") {
            @Override
            public void initTranslations() {
                canNotUpdateTitle = messages.getString("canNotUpdateTitle");
                canNotUpdateMessage = messages.getString("canNotUpdateMessage");
            }
        };
        translation.initTranslations();
    }
}

package com.doos.update_module.update;


import com.doos.commons.core.ApplicationConstants;
import com.doos.update_module.core.Main;
import com.doos.update_module.gui.UpdateDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.bridj.Pointer;
import org.bridj.PointerIO;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.shell.ITaskbarList3;
import org.bridj.jawt.JAWTUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import static com.doos.commons.core.ApplicationConstants.WINDOWS_WEBLOCOPENER_SETUP_NAME;
import static com.doos.commons.utils.Logging.getCurrentClassName;
import static com.doos.commons.utils.UserUtils.showErrorMessageToUser;

/**
 * Created by Eugene Zrazhevsky on 03.11.2016.
 */
@SuppressWarnings({"ALL", "ResultOfMethodCallIgnored"})
public class Updater {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    private static final String GITHUB_URL = "https://api.github.com/repos/benchdoos/WeblocOpener/releases/latest";
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static File installerFile = null;
    private static HttpsURLConnection connection = null;
    private AppVersion appVersion = null;


    public Updater() throws IOException, NullPointerException {
        try {
            getConnection();
            if (!connection.getDoOutput()) {
                connection.setDoOutput(true);
            }
            if (!connection.getDoInput()) {
                connection.setDoInput(true);
            }

        } catch (IOException e) {
            log.warn(e);
        }


        String input;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODING));

        input = bufferedReader.readLine();

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(input).getAsJsonObject();

        appVersion = new AppVersion();
        formAppVersionFromJson(root);

    }

    public static void canNotConnectManage(Exception e) {
        String message = "Can not connect to api.github.com";
        log.warn(message, e);
        if (Main.mode != Main.Mode.SILENT) {
            showErrorMessageToUser(null, "Can not Update", message);
        }
    }

    public static int startUpdate(AppVersion appVersion) throws IOException {
        installerFile = new File(ApplicationConstants.UPDATE_PATH_FILE + "WeblocOpenerSetupV"
                + appVersion.getVersion() + ".exe");
        if (!Thread.currentThread().isInterrupted()) {
            if (!installerFile.exists() || installerFile.length() != appVersion.getSize()) {

                installerFile.delete();
                installerFile = downloadNewVersionInstaller(appVersion);


                return -999;


            }
            if (!Thread.currentThread().isInterrupted()) {
                int installationResult = 0;

                try {
                    if (!Thread.currentThread().isInterrupted()) {
                        if (installerFile.length() == appVersion.getSize()) {
                            installationResult = update(installerFile);
                        }
                    }
                } catch (IOException e) {
                    if (e.getMessage().contains("CreateProcess error=193")) {
                        try {
                            if (!Thread.currentThread().isInterrupted()) {
                                installerFile.delete();
                                installerFile = downloadNewVersionInstaller(appVersion); //Fixes corrupt file
                                installationResult = update(installerFile);
                            } else {
                                return -999;
                            }
                        } catch (IOException e1) {
                            if (e1.getMessage().contains("CreateProcess error=193")) {
                                installerFile.delete();
                                return 193;
                            }
                        }
                    }
                }
                deleteFileIfSuccess(installationResult);
                return installationResult;
            } else {
                return -999;
            }

        } else {
            return -999;
        }
    }

    private static void deleteFileIfSuccess(int installationResult) {
        if (installationResult == 0) {
            installerFile.deleteOnExit();
        }
    }

    private static int update(File file) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        UpdateDialog.updateDialog.buttonCancel.setEnabled(false);
        Process updateProcess;
        updateProcess = runtime.exec(file.getAbsolutePath() + ApplicationConstants.APP_INSTALL_SILENT_KEY);

        int result = -1;
        try {
            if (!Thread.currentThread().isInterrupted()) {
                result = updateProcess.waitFor();
                log.warn("Update interrupted by user.");
            }
            return result;
        } catch (InterruptedException e) {
            log.warn(e);
            return -999;
        }

    }

    private static File downloadNewVersionInstaller(AppVersion appVersion) throws IOException {
        JProgressBar progressBar = null;
        if (UpdateDialog.updateDialog != null) {
            progressBar = UpdateDialog.updateDialog.progressBar1;
        }

        ITaskbarList3 list = null;
        Pointer<?> hwnd;

        try {
            list = COMRuntime.newInstance(ITaskbarList3.class);
        } catch (ClassNotFoundException ignore) {/*WINDOWS<WINDOWS 7*/}

        long hwndVal = JAWTUtils.getNativePeerHandle(UpdateDialog.updateDialog);
        hwnd = Pointer.pointerToAddress(hwndVal, PointerIO.getSizeTInstance());


        if (progressBar != null) {
            progressBar.setStringPainted(true);
        }
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {

            in = new BufferedInputStream(new URL(appVersion.getDownloadUrl()).openStream());
            try {
                fout = new FileOutputStream(installerFile);

                final int bufferLength = 1024 * 1024;
                final byte data[] = new byte[bufferLength];
                int count;
                int progress = 0;
                while ((count = in.read(data, 0, bufferLength)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        installerFile.delete();
                        if (progressBar != null) {
                            progressBar.setValue(0);
                            if (list != null) {
                                //noinspection unchecked
                                list.SetProgressValue((Pointer) hwnd, progressBar.getValue(),
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
                            if (list != null) {
                                //noinspection unchecked
                                list.SetProgressValue((Pointer) hwnd, progressBar.getValue(),
                                        progressBar.getMaximum());
                            }
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                if (installerFile.exists() && installerFile.getName().contains(WINDOWS_WEBLOCOPENER_SETUP_NAME)) { //TODO FIX
                    // HERE
                    installerFile.delete();
                    fout = new FileOutputStream(installerFile);
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }

            if (Thread.currentThread().isInterrupted()) {
                installerFile.delete();
            }
            if (list != null) {
                list.Release();
            }
        }

        return installerFile;
    }

    public void formAppVersionFromJson(JsonObject root) {
        appVersion.setVersion(root.getAsJsonObject().get("tag_name").getAsString());

        JsonArray asserts = root.getAsJsonArray("assets");
        for (JsonElement assert_ : asserts) {
            JsonObject userObject = assert_.getAsJsonObject();
            if (userObject.get("name").getAsString().equals("WeblocOpenerSetup.exe")) {
                appVersion.setDownloadUrl(userObject.get("browser_download_url").getAsString());
                appVersion.setSize(userObject.get("size").getAsLong());
            }
        }
    }

    private HttpsURLConnection getConnection() throws IOException {
        URL url = new URL(GITHUB_URL);

        connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(500);
        return connection;
    }

    public AppVersion getAppVersion() {
        return appVersion;
    }
}

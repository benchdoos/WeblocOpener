package update;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utils.ApplicationConstants;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

/**
 * Created by Eugene Zrazhevsky on 03.11.2016.
 */
public class Updater {
    private static final String githubUrl = "https://api.github.com/repos/benchdoos/WeblocOpener/releases/latest";
    private static HttpsURLConnection connection = null;
    private static URL url = null;
    private static File installerFile = null;
    private AppVersion appVersion = null;

    public Updater() {
        try {
            getConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input = null;
            try {
                input = bufferedReader.readLine();
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
            bufferedReader.close();

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(input).getAsJsonObject();

            appVersion = new AppVersion();

            appVersion.setVersion(root.getAsJsonObject().get("tag_name").getAsString());

            JsonArray asserts = root.getAsJsonArray("assets");
            for (JsonElement assert_ : asserts) {
                JsonObject userObject = assert_.getAsJsonObject();
                if (userObject.get("name").getAsString().equals("WeblocOpenerSetup.exe")) {
                    appVersion.setDownloadUrl(userObject.get("browser_download_url").getAsString());
                    String udate = userObject.get("updated_at").getAsString();
                    appVersion.setUpdateDate(Date.from(Instant.parse(udate)));
                    appVersion.setSize(userObject.get("size").getAsInt());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int startUpdate(AppVersion appVersion, JProgressBar progressBar) {
        installerFile = new File(ApplicationConstants.UPDATE_PATH_FILE
                + "WeblocOpenerSetupV" + appVersion.getVersion() + ".exe");
        if (!installerFile.exists()) {
            installerFile = downloadNewVersionInstaller(appVersion, progressBar);
        }
        int installationResult = update(installerFile);
        deleteFileIfSuccess(installationResult);
        return installationResult;

    }

    private static void deleteFileIfSuccess(int installationResult) {
        if (installationResult == 0) {
            installerFile.deleteOnExit();
        }
    }

    private static int update(File file) {
        Runtime runtime = Runtime.getRuntime();
        Process updateProcess;
        try {
            updateProcess = runtime.exec(file.getAbsolutePath() + "");

            int result;
            try {
                result = updateProcess.waitFor();
                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 1;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        }
    }

    private static File downloadNewVersionInstaller(AppVersion appVersion, JProgressBar progressBar) {
               /*try {
            FileUtils.copyURLToFile(new URL(appVersion.getDownloadUrl()),
                    new File(ApplicationConstants.UPDATE_PATH_FILE + appVersion.getVersion() + "setup.exe"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        try {
            progressBar.setStringPainted(true);
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try {

                in = new BufferedInputStream(new URL(appVersion.getDownloadUrl()).openStream());
                fout = new FileOutputStream(installerFile);

                final byte data[] = new byte[1024];
                int count;
                int progress = 0;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                    progress += count;
                    int prg = (int) (((double) progress / appVersion.getSize()) * 100);

                    progressBar.setValue(prg);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return installerFile;
    }

    public HttpsURLConnection getConnection() throws IOException {
        url = new URL(githubUrl);
        if (connection == null) {
            connection = (HttpsURLConnection) url.openConnection();
        }
        return connection;
    }

    public AppVersion getAppVersion() {
        return appVersion;
    }
}

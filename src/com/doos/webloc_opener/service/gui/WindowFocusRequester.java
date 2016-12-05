package com.doos.webloc_opener.service.gui;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.doos.commons.utils.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 * <p>
 * Maybe there is a better solution, but I didn't find in out yet. Any ideas?
 */


public class WindowFocusRequester {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    public static String requestFocusOnWindowScript(String windowTitle) {
        String script = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n" +
                "WshShell.AppActivate(\"" + windowTitle + "\")";
        log.debug("Script created: " + script);
        return script;
    }

    public static boolean runScript(String script) {
        FileOutputStream fileOutputStream = null;
        File file = null;
        try {
            file = File.createTempFile("WeblocOpenerScript", ".vbs");
            file.deleteOnExit();
            //fileOutputStream = new FileWriter(file);
            fileOutputStream = new FileOutputStream(file);
            log.debug("Temp file created at: " + file.getAbsolutePath());
            byte[] contentInBytes = script.getBytes(Charset.forName("UTF-8"));
            fileOutputStream.write(contentInBytes);

        } catch (Exception e) {
            log.warn("Can not create script: " + script, e);
            return false;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException ignore) {/*NOP*/}
        }
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("wscript " + file.getPath());
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            log.warn("Can not run script: " + script, e);

        }
        return p != null && (p.exitValue() == 1);
    }
}

package com.doos.webloc_opener.service.gui;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.doos.webloc_opener.service.Logging.getCurrentClassName;

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
        FileWriter fw = null;
        try {
            File file = File.createTempFile("WeblocOpenerScript", ".vbs");
            file.deleteOnExit();
            fw = new java.io.FileWriter(file);
            log.debug("Temp file created at: " + file.getAbsolutePath());
            fw.write(script);

            Process p = Runtime.getRuntime().exec("wscript " + file.getPath());
            p.waitFor();
            return (p.exitValue() == 1);
        } catch (Exception e) {
            log.warn("Can not run script: " + script, e);
            return false;
        } finally {
            try {
                fw.close();
            } catch (IOException ignore) {/*NOP*/}
        }
    }
}

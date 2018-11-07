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

package com.github.benchdoos.weblocopener.service.gui;

import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;


/**
 * Created by Eugene Zrazhevsky on 02.11.2016.
 * <p>
 * Maybe there is a better solution, but I didn't find in out yet. Any ideas?
 */


class WindowFocusRequester {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());


    public static String requestFocusOnWindowScript(String windowTitle) {
        String script = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n" +
                "WshShell.AppActivate(\"" + windowTitle + "\")";
        log.debug("Script created: " + script);
        return script;
    }

    public static boolean runScript(String script) {
        FileOutputStream fileOutputStream = null;
        File file;
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

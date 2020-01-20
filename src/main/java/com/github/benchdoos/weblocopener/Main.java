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

package com.github.benchdoos.weblocopener;

import com.github.benchdoos.weblocopener.core.Application;
import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.utils.Logging;
import com.github.benchdoos.weblocopener.utils.notification.NotificationManager;
import com.github.benchdoos.weblocopenercore.core.constants.ApplicationArgument;
import com.github.benchdoos.weblocopenercore.utils.system.OS;
import com.github.benchdoos.weblocopenercore.utils.system.SystemUtils;
import com.github.benchdoos.weblocopenercore.utils.system.UnsupportedSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static com.github.benchdoos.weblocopenercore.core.constants.ApplicationConstants.UPDATER_APPLICATION_NAME;
import static com.github.benchdoos.weblocopenercore.core.constants.ApplicationConstants.WEBLOCOPENER_APPLICATION_NAME;

public class Main {
    private static MODE currentMode;
    private static Logger log;


    public Main(String[] args) {
        System.out.println("WeblocOpener starting with args: " + Arrays.toString(args));

        currentMode = manageMode(args);
        initLogging(currentMode);
        try {
            SystemUtils.checkIfSystemIsSupported();

            new Application(args);
        } catch (UnsupportedSystemException e) {
            log.fatal("System not supported", e);
            final String translatedString = Translation.getTranslatedString("CommonsBundle", "systemNotSupported");
            final String message = translatedString + " " + OS.getCurrentOS().name();

            NotificationManager.getForcedNotification(null).showErrorNotification(message, message);
        } catch (Exception e) {
            log.fatal("System exited with exception", e);
        }
    }


    private static void initLogging(MODE mode) {
        switch (mode) {
            case WEBLOCOPENER:
                new Logging(WEBLOCOPENER_APPLICATION_NAME);
                break;
            case UPDATE:
                new Logging(UPDATER_APPLICATION_NAME);
                break;
        }
        log = LogManager.getLogger(Logging.getCurrentClassName());
    }

    public static void main(String[] args) {
        new Main(args);
    }

    public static MODE getCurrentMode() {
        return currentMode;
    }

    private MODE manageMode(String[] args) {
        if (args.length > 0) {
            final String arg = args[0];

            final boolean updateArg = ApplicationArgument.getByArgument(arg).equals(ApplicationArgument.OPENER_UPDATE_ARGUMENT);
            if (updateArg) {
                return MODE.UPDATE;
            }
        }
        return MODE.WEBLOCOPENER;
    }

    enum MODE {WEBLOCOPENER, UPDATE}
}

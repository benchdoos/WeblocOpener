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

package com.github.benchdoos.weblocopener.core;

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Application {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public Application(String[] args) {
        log.info("{} starts in mode: {}", ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME, Main.getCurrentMode());
        log.info("{} starts with arguments: {}", ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME, Arrays.toString(args));

    }
}

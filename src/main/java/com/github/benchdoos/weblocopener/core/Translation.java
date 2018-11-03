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

import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;


public abstract class Translation {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    public final ResourceBundle messages;
    private final String bundlePath;

    protected Translation(String bundlePath) {
        this.bundlePath = bundlePath;
        messages = getTranslation();
    }

    public abstract void initTranslations();

    private ResourceBundle getTranslation() {
        Locale currentLocale = Locale.getDefault();

        log.debug("Locale: " + currentLocale.getCountry() + " " + currentLocale.getLanguage());
        final ResourceBundle bundle = ResourceBundle.getBundle(bundlePath,
                currentLocale);
        log.debug("bundle: " + bundle.getBaseBundleName());
        return bundle;
    }
}

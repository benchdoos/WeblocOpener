/*
 * Copyright 2018 Eugeny Zrazhevsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.benchdoos.weblocopener.commons.core;

import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

import static com.github.benchdoos.weblocopener.commons.utils.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 01.12.2016.
 */

public abstract class Translation {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

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

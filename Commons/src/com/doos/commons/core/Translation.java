package com.doos.commons.core;

import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

import static com.doos.commons.utils.Logging.getCurrentClassName;

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

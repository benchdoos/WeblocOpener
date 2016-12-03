package com.doos.settings_manager;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Eugene Zrazhevsky on 01.12.2016.
 */
public abstract class Translation {

    public final ResourceBundle messages;
    private final String bundlePath;

    public Translation(String bundlePath) {
        this.bundlePath = bundlePath;
        messages = getTranslation();
    }

    public abstract void initTranslations();

    private ResourceBundle getTranslation() {
        Locale currentLocale = Locale.getDefault();

        System.out.println("Locale: " + currentLocale.getCountry() + " " + currentLocale.getLanguage());
        final ResourceBundle bundle = ResourceBundle.getBundle(bundlePath,
                                                               currentLocale);
        System.out.println("bundle: " + bundle.getBaseBundleName());
        return bundle;
    }
}

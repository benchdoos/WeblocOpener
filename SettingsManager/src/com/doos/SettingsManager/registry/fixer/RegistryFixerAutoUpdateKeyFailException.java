package com.doos.SettingsManager.registry.fixer;

import com.doos.SettingsManager.registry.RegistryManager;

/**
 * Created by Eugene Zrazhevsky on 23.11.2016.
 */
public class RegistryFixerAutoUpdateKeyFailException extends RegistryFixerException {

    public RegistryFixerAutoUpdateKeyFailException() {
        super("Failed to fix " + RegistryManager.KEY_AUTO_UPDATE +
                      " " +
                      "value. ");
    }

    public RegistryFixerAutoUpdateKeyFailException(String message) {
        super("Failed to fix " + RegistryManager.KEY_AUTO_UPDATE +
                      " " +
                      "value. " + message);
    }

    public RegistryFixerAutoUpdateKeyFailException(String message, Throwable caused) {
        super("Failed to fix " + RegistryManager.KEY_AUTO_UPDATE +
                      " " +
                      "value. " + message, caused);
    }
}

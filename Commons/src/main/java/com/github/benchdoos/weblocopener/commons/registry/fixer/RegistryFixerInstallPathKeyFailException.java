package com.github.benchdoos.weblocopener.commons.registry.fixer;

import com.github.benchdoos.weblocopener.commons.registry.RegistryManager;

/**
 * Created by Eugene Zrazhevsky on 23.11.2016.
 */
public class RegistryFixerInstallPathKeyFailException extends RegistryFixerException {

    public RegistryFixerInstallPathKeyFailException() {
        super("Failed to fix " + RegistryManager.KEY_CURRENT_VERSION + " key value. ");
    }

    public RegistryFixerInstallPathKeyFailException(String message) {
        super("Failed to fix " + RegistryManager.KEY_CURRENT_VERSION + " key value. " + message);
    }

    public RegistryFixerInstallPathKeyFailException(String message, Throwable caused) {
        super("Failed to fix " + RegistryManager.KEY_CURRENT_VERSION + " key value. " + message, caused);
    }
}

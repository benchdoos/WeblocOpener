package com.doos.utils.registry.fixer;

import com.doos.utils.registry.RegistryManager;

/**
 * Created by Eugene Zrazhevsky on 23.11.2016.
 */
public class RegistryFixerAppVersionKeyFailException extends RegistryFixerException {

    public RegistryFixerAppVersionKeyFailException() {
        super("Failed to fix " + RegistryManager.KEY_CURRENT_VERSION + " key value. ");
    }

    public RegistryFixerAppVersionKeyFailException(String message) {
        super("Failed to fix " + RegistryManager.KEY_CURRENT_VERSION + " key value. " + message);
    }

    public RegistryFixerAppVersionKeyFailException(String message, Throwable caused) {
        super("Failed to fix " + RegistryManager.KEY_CURRENT_VERSION + " key value. " + message, caused);
    }
}

package com.github.benchdoos.weblocopener.commons.registry.fixer;

import com.github.benchdoos.weblocopener.commons.registry.RegistryException;

/**
 * Created by Eugene Zrazhevsky on 23.11.2016.
 */
@SuppressWarnings("WeakerAccess")
public class RegistryFixerException extends RegistryException {
    public RegistryFixerException() {
        super("[REGISTRY FIXER]");
    }

    public RegistryFixerException(String message) {
        super("[REGISTRY FIXER] " + message);
    }

    public RegistryFixerException(String message, Throwable caused) {
        super("[REGISTRY FIXER] " + message, caused);
    }
}

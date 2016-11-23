package com.doos.utils.registry.fixer;

import com.doos.utils.registry.RegistryException;

/**
 * Created by Eugene Zrazhevsky on 23.11.2016.
 */
public class RegistryFixerException extends RegistryException {
    public RegistryFixerException() {
        super("[REGISTRY FIXER]");
    }

    public RegistryFixerException(String message) {
        super("[REGISTRY FIXER] " + message);
    }

    public RegistryFixerException(String message, Throwable caused) { super("[REGISTRY FIXER] " + message, caused);}
}

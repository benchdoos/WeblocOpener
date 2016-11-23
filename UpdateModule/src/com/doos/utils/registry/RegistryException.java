package com.doos.utils.registry;

/**
 * Created by Eugene Zrazhevsky on 21.11.2016.
 */
public class RegistryException extends Exception {
    public RegistryException() {
        super("[Registry] ");
    }

    public RegistryException(String message) {
        super("[Registry] " + message);
    }

    public RegistryException(String message, Throwable cause) {
        super("[Registry] " + message, cause);
    }
}

package com.github.benchdoos.weblocopener.commons.registry;

/**
 * Created by Eugene Zrazhevsky on 21.11.2016.
 */
public class RegistryCanNotReadInfoException extends RegistryException {
    public RegistryCanNotReadInfoException() {
        super();
    }

    public RegistryCanNotReadInfoException(String message) {
        super(message);
    }

    public RegistryCanNotReadInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}

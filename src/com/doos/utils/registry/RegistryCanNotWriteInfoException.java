package com.doos.utils.registry;

/**
 * Created by Eugene Zrazhevsky on 21.11.2016.
 */
public class RegistryCanNotWriteInfoException extends RegistryException {
    public RegistryCanNotWriteInfoException() {
        super();
    }

    public RegistryCanNotWriteInfoException(String message) {
        super(message);
    }

    public RegistryCanNotWriteInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}

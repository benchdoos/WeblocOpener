package com.doos.commons.utils.system;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class UnsupportedOsSystemException extends UnsupportedSystemException {
    public UnsupportedOsSystemException() {
        super("OS is not supported.");
    }

    public UnsupportedOsSystemException(String message) {
        super("OS is not supported. " + message);
    }

    public UnsupportedOsSystemException(String message, Throwable cause) {
        super("OS is not supported. " + message, cause);
    }
}

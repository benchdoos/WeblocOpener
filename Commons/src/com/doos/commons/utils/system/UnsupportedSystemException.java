package com.doos.commons.utils.system;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class UnsupportedSystemException extends SystemException {
    public UnsupportedSystemException() {
        super();
    }

    public UnsupportedSystemException(String message) {
        super(message);
    }

    public UnsupportedSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

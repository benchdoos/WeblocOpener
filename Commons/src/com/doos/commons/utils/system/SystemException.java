package com.doos.commons.utils.system;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class SystemException extends Exception {

    public SystemException() {
        super("[SystemException]");
    }

    public SystemException(String message) {
        super("[SystemException] " + message);
    }

    public SystemException(String message, Throwable cause) {
        super("[SystemException] " + message, cause);
    }
}

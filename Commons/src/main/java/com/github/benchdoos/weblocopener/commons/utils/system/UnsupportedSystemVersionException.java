package com.github.benchdoos.weblocopener.commons.utils.system;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class UnsupportedSystemVersionException extends UnsupportedSystemException {
    private static final String DEFAULT_MESSAGE = "System version is not supported yet. \nversion:'"
            + System.getProperty("os.version") + "', needed: '" + SystemUtils.MINIMUM_WINDOWS_VERSION + "'";

    public UnsupportedSystemVersionException() {
        super(DEFAULT_MESSAGE);
    }

    public UnsupportedSystemVersionException(String message) {
        super(DEFAULT_MESSAGE + message);
    }

    public UnsupportedSystemVersionException(String message, Throwable cause) {
        super(DEFAULT_MESSAGE + message, cause);
    }
}

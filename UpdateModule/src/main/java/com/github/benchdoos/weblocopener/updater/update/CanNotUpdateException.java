package com.github.benchdoos.weblocopener.updater.update;

/**
 * Created by User on 21.08.2017.
 */
public class CanNotUpdateException extends Exception {
    public CanNotUpdateException() {
        super("[CanNotUpdateException]");
    }

    public CanNotUpdateException(String message) {
        super("[CanNotUpdateException] " + message);
    }

    public CanNotUpdateException(String message, Throwable cause) {
        super("[CanNotUpdateException] " + message, cause);
    }
}

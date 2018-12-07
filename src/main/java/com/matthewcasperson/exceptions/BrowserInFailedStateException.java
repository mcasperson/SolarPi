package com.matthewcasperson.exceptions;

public class BrowserInFailedStateException extends RuntimeException {
    public BrowserInFailedStateException() {
    }

    public BrowserInFailedStateException(String message) {
        super(message);
    }

    public BrowserInFailedStateException(Throwable cause) {
        super(cause);
    }

    public BrowserInFailedStateException(String message, Throwable cause) {
        super(message, cause);
    }
}

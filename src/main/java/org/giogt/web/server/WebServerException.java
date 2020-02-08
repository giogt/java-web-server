package org.giogt.web.server;

public class WebServerException extends Exception {
    private static final long serialVersionUID = 1L;

    public WebServerException() {
    }

    public WebServerException(String message) {
        super(message);
    }

    public WebServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServerException(Throwable cause) {
        super(cause);
    }

    public WebServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

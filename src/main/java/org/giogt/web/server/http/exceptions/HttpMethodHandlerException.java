package org.giogt.web.server.http.exceptions;

public class HttpMethodHandlerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HttpMethodHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.giogt.web.server.http.exceptions;

import java.text.MessageFormat;

public class HttpRequestParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE = "HTTP request cannot be parsed: {0}";

    protected HttpRequestParseException(String message) {
        super(message);
    }

    protected HttpRequestParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public static HttpRequestParseException createNew(String message) {
        return createNew(message, null);
    }

    public static HttpRequestParseException createNew(
            String message,
            Throwable cause) {

        return new HttpRequestParseException(
                MessageFormat.format(MESSAGE, message),
                cause);
    }
}

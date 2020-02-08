package org.giogt.web.server.http.exceptions;

import org.giogt.web.server.http.model.HttpMethod;

import java.io.IOException;
import java.text.MessageFormat;

public class HttpMethodHandlerIOException extends HttpMethodHandlerException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "IO error while handling method {0}";

    private HttpMethodHandlerIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public static HttpMethodHandlerIOException forMethod(
            HttpMethod method,
            IOException e) {
        return new HttpMethodHandlerIOException(
                MessageFormat.format(MESSAGE, method),
                e);
    }
}

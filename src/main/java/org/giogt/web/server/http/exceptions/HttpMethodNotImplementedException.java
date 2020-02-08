package org.giogt.web.server.http.exceptions;

import org.giogt.web.server.http.model.HttpMethod;

import java.text.MessageFormat;

public class HttpMethodNotImplementedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "HTTP method is not implemented by this server: {0}";

    private HttpMethodNotImplementedException(String message) {
        super(message);
    }

    public static HttpMethodNotImplementedException forMethod(HttpMethod method) {
        return new HttpMethodNotImplementedException(
                MessageFormat.format(MESSAGE, method));
    }
}

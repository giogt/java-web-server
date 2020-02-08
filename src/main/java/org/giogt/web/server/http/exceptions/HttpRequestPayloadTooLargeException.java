package org.giogt.web.server.http.exceptions;

import java.text.MessageFormat;

public class HttpRequestPayloadTooLargeException extends HttpRequestParseException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE = "HTTP request payload is too large [limit={0} bytes]";

    private HttpRequestPayloadTooLargeException(String message) {
        super(message);
    }

    public static HttpRequestPayloadTooLargeException forLimit(int limit) {
        return new HttpRequestPayloadTooLargeException(
                MessageFormat.format(MESSAGE, limit));
    }
}

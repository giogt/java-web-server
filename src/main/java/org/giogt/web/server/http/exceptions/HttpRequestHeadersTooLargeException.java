package org.giogt.web.server.http.exceptions;

import java.text.MessageFormat;

public class HttpRequestHeadersTooLargeException extends HttpRequestParseException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE = "HTTP request headers are too large [limit={0} chars]";

    private HttpRequestHeadersTooLargeException(String message) {
        super(message);
    }

    public static HttpRequestHeadersTooLargeException forLimit(int limit) {
        return new HttpRequestHeadersTooLargeException(
                MessageFormat.format(MESSAGE, limit));
    }
}

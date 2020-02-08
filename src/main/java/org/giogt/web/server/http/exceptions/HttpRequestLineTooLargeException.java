package org.giogt.web.server.http.exceptions;

import java.text.MessageFormat;

public class HttpRequestLineTooLargeException extends HttpRequestParseException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE = "HTTP request line is too large [limit={0} chars]";

    private HttpRequestLineTooLargeException(String message) {
        super(message);
    }

    public static HttpRequestLineTooLargeException forLimit(int limit) {
        return new HttpRequestLineTooLargeException(
                MessageFormat.format(MESSAGE, limit));
    }
}

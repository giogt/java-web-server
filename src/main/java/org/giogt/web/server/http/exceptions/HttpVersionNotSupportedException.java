package org.giogt.web.server.http.exceptions;

import org.giogt.web.server.http.model.HttpVersion;

import java.text.MessageFormat;

public class HttpVersionNotSupportedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "HTTP version not supported: {0} (supported versions: {1})";

    private HttpVersionNotSupportedException(String message) {
        super(message);
    }

    public static HttpVersionNotSupportedException forVersionString(String versionString) {
        return new HttpVersionNotSupportedException(
                MessageFormat.format(
                        MESSAGE,
                        versionString,
                        String.format("%s, %s", HttpVersion.VERSION_1_0, HttpVersion.VERSION_1_1)));
    }
}

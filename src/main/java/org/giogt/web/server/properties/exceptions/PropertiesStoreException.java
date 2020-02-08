package org.giogt.web.server.properties.exceptions;

public class PropertiesStoreException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PropertiesStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

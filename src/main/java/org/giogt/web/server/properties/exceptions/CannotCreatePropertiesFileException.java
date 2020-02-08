package org.giogt.web.server.properties.exceptions;

public class CannotCreatePropertiesFileException extends PropertiesStoreException {
    private static final long serialVersionUID = 1L;

    public CannotCreatePropertiesFileException(String message, Throwable cause) {
        super(message, cause);
    }
}

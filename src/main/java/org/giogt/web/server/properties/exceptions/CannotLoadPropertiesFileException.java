package org.giogt.web.server.properties.exceptions;

public class CannotLoadPropertiesFileException extends PropertiesStoreException {
    private static final long serialVersionUID = 1L;

    public CannotLoadPropertiesFileException(String message, Throwable cause) {
        super(message, cause);
    }
}

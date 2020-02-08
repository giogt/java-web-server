package org.giogt.web.server.properties;

public class PropertySerializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PropertySerializationException() {
    }

    public PropertySerializationException(String message) {
        super(message);
    }

    public PropertySerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertySerializationException(Throwable cause) {
        super(cause);
    }
}

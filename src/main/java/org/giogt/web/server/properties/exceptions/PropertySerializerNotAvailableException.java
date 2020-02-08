package org.giogt.web.server.properties.exceptions;

import java.text.MessageFormat;

public class PropertySerializerNotAvailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private static final String MESSAGE = "No property serializer available for type <{0}>";

    PropertySerializerNotAvailableException(String message) {
        super(message);
    }

    public static PropertySerializerNotAvailableException forType(Class<?> type) {
        return new PropertySerializerNotAvailableException(MessageFormat.format(MESSAGE, type));
    }
}

package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.giogt.web.server.properties.PropertySerializer;

public class StringPropertySerializer implements PropertySerializer<String> {

    @Override
    public Class<? extends String> handledType() {
        return String.class;
    }

    @Override
    public String fromString(
            MappingContext context,
            String stringValue)
            throws PropertySerializationException {

        return stringValue;
    }

    @Override
    public String toString(
            MappingContext context,
            String value)
            throws PropertySerializationException {

        return value;
    }
}

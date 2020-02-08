package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.giogt.web.server.properties.PropertySerializer;

public class IntegerPropertySerializer implements PropertySerializer<Integer> {

    @Override
    public Class<? extends Integer> handledType() {
        return Integer.class;
    }

    @Override
    public Integer fromString(
            MappingContext context,
            String stringValue)
            throws PropertySerializationException {

        if (stringValue == null) {
            return null;
        }

        try {
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new PropertySerializationException(
                    "cannot parse property value <" + stringValue + "> to integer",
                    e);
        }
    }

    @Override
    public String toString(
            MappingContext context,
            Integer value)
            throws PropertySerializationException {

        if (value == null) {
            return null;
        }
        return Integer.toString(value);
    }
}

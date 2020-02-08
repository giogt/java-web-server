package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.giogt.web.server.properties.PropertySerializer;

public class BooleanPropertySerializer implements PropertySerializer<Boolean> {

    @Override
    public Class<? extends Boolean> handledType() {
        return Boolean.class;
    }

    @Override
    public Boolean fromString(
            MappingContext context,
            String stringValue)
            throws PropertySerializationException {

        if (stringValue == null) {
            return null;
        }

        Boolean value;
        if (stringValue.equalsIgnoreCase(BooleanValues.TRUE_STRING)) {
            value = Boolean.TRUE;
        } else if (stringValue.equalsIgnoreCase(BooleanValues.FALSE_STRING)) {
            value = Boolean.FALSE;
        } else {
            throw new PropertySerializationException(
                    "cannot parse property value <" + stringValue + "> to boolean [allowed value: true, false]");
        }

        return value;
    }

    @Override
    public String toString(
            MappingContext context,
            Boolean value)
            throws PropertySerializationException {

        if (value == null) {
            return null;
        }

        String propertyValue;
        if (value) {
            propertyValue = BooleanValues.TRUE_STRING;
        } else {
            propertyValue = BooleanValues.FALSE_STRING;
        }

        return propertyValue;
    }
}

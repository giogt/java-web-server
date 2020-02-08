package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.giogt.web.server.properties.PropertySerializer;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class PathPropertySerializer implements PropertySerializer<Path> {
    @Override
    public Class<? extends Path> handledType() {
        return Path.class;
    }

    @Override
    public Path fromString(
            MappingContext context,
            String stringValue)
            throws PropertySerializationException {

        if (stringValue == null) {
            return null;
        }

        try {
            return Path.of(stringValue);
        } catch (InvalidPathException e) {
            throw new PropertySerializationException(
                    "cannot parse property value <" + stringValue + "> to path",
                    e);
        }
    }

    @Override
    public String toString(
            MappingContext context,
            Path value)
            throws PropertySerializationException {

        if (value == null) {
            return null;
        }

        return value.toString();
    }
}

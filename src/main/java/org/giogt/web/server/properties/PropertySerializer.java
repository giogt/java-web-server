package org.giogt.web.server.properties;


public interface PropertySerializer<T> {

    /**
     * Returns the type of objects this serializer can handle.
     * <p>
     * This can also be a more generic (super) type of objects.
     */
    Class<? extends T> handledType();

    T fromString(
            MappingContext context,
            String stringValue)
            throws PropertySerializationException;

    String toString(
            MappingContext context,
            T value)
            throws PropertySerializationException;

}

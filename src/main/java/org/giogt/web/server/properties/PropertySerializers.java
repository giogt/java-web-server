package org.giogt.web.server.properties;

import org.giogt.web.server.properties.exceptions.PropertySerializerNotAvailableException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class PropertySerializers {

    private final Map<Class<?>, PropertySerializer<?>> propertySerializers;

    public PropertySerializers() {
        this.propertySerializers = new HashMap<>();
        initSerializers(propertySerializers);
    }

    @SuppressWarnings("rawtypes")
    private void initSerializers(Map<Class<?>, PropertySerializer<?>> propertySerializers) {
        ServiceLoader<PropertySerializer> serviceLoader = ServiceLoader.load(PropertySerializer.class);
        for (PropertySerializer<?> propertySerializer : serviceLoader) {
            propertySerializers.put(propertySerializer.handledType(), propertySerializer);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> PropertySerializer<T> forType(Class<T> type) {
        return (PropertySerializer<T>) propertySerializers.get(type);
    }

    public <T> PropertySerializer<T> forTypeSafe(Class<T> type) {
        PropertySerializer<T> propertySerializer = forType(type);
        if (propertySerializer == null) {
            throw PropertySerializerNotAvailableException.forType(type);
        }
        return propertySerializer;
    }
}

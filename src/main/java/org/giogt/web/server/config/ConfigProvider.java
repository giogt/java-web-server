package org.giogt.web.server.config;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertiesStore;
import org.giogt.web.server.properties.Property;
import org.giogt.web.server.properties.PropertySerializer;
import org.giogt.web.server.properties.PropertySerializers;

import java.lang.reflect.Field;

public class ConfigProvider {

    private final PropertySerializers propertySerializers;
    private final PropertiesStore propertiesStore;

    public ConfigProvider(
            PropertySerializers propertySerializers,
            PropertiesStore propertiesStore) {

        this.propertySerializers = propertySerializers;
        this.propertiesStore = propertiesStore;
    }

    public WebServerConfig getConfig() {
        WebServerConfig config = new WebServerConfig();
        Class<? super WebServerConfig> currentClass = WebServerConfig.class;

        while (currentClass != null) {
            processFields(currentClass, config);
            currentClass = currentClass.getSuperclass();
        }
        return config;
    }

    private void processFields(
            Class<?> currentClass, WebServerConfig config) {

        Field[] fields = currentClass.getDeclaredFields();
        for (Field field : fields) {
            Property property = field.getAnnotation(Property.class);
            if (property != null) {
                String value = propertiesStore.getProperty(property.key());
                if (value == null) {
                    value = property.defaultValue();
                }
                PropertySerializer<?> propertySerializer = propertySerializers.forType(field.getType());
                MappingContext mappingContext = new MappingContext(field.getType());
                field.setAccessible(true);
                try {
                    field.set(config, propertySerializer.fromString(mappingContext, value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

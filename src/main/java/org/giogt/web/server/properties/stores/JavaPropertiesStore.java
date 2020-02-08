package org.giogt.web.server.properties.stores;

import org.giogt.web.server.properties.PropertiesStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A properties store providing Java system properties.
 */
public class JavaPropertiesStore implements PropertiesStore {

    private final String prefix;

    JavaPropertiesStore(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getProperty(String key) {
        key = buildKeyWithPrefix(key);
        return System.getProperty(key);
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> propertiesMap = new HashMap<>();

        Properties systemProperties = System.getProperties();
        for (String key : systemProperties.stringPropertyNames()) {
            // if prefix is specified, consider only properties starting with prefix
            if (prefix == null || key.startsWith(prefix)) {
                propertiesMap.put(key, getProperty(key));
            }
        }

        return propertiesMap;
    }

    @Override
    public void refresh() {
        // java properties are always up to date => no need to refresh them
    }

    String buildKeyWithPrefix(String key) {
        if (prefix != null) {
            key = prefix + key;
        }
        return key;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String prefix;

        public Builder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public JavaPropertiesStore build() {
            return new JavaPropertiesStore(prefix);
        }
    }
}

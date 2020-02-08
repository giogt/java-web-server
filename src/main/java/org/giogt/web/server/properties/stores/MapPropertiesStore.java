package org.giogt.web.server.properties.stores;

import org.giogt.web.server.properties.PropertiesStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A properties store providing the properties that are present in the map it
 * is initialized with.
 */
public class MapPropertiesStore implements PropertiesStore {

    private final Map<String, String> propertiesMap;

    MapPropertiesStore(Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    @Override
    public String getProperty(String key) {
        return propertiesMap.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(propertiesMap);
    }

    @Override
    public void refresh() {
        // in memory map properties are always up to date => no need to refresh them
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, String> map = new HashMap<>();

        public Builder addProperty(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Builder addProperties(Map<String, String> propertiesMap) {
            map.putAll(propertiesMap);
            return this;
        }

        public MapPropertiesStore build() {
            return new MapPropertiesStore(map);
        }
    }
}

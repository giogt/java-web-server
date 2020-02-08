package org.giogt.web.server.properties.stores;

import org.giogt.web.server.properties.PropertiesStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositePropertiesStore implements PropertiesStore {

    private final List<PropertiesStore> propertiesStores;

    CompositePropertiesStore(List<PropertiesStore> propertiesStores) {
        this.propertiesStores = propertiesStores;
    }

    @Override
    public String getProperty(String key) {
        for (PropertiesStore propertiesStore : propertiesStores) {
            String value = propertiesStore.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> propertiesMap = new HashMap<>();
        for (PropertiesStore propertiesStore : propertiesStores) {
            Map<String, String> currentPropertiesMap = propertiesStore.getProperties();
            propertiesMap.putAll(currentPropertiesMap);
        }
        return propertiesMap;
    }

    @Override
    public void refresh() {
        for (PropertiesStore propertiesStore : propertiesStores) {
            propertiesStore.refresh();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<PropertiesStore> propertiesStores;

        public Builder() {
            propertiesStores = new ArrayList<>();
        }

        public Builder addPropertiesStore(PropertiesStore propertiesStore) {
            propertiesStores.add(propertiesStore);
            return this;
        }

        public Builder addPropertiesStores(
                Collection<? extends PropertiesStore> propertiesStores) {

            this.propertiesStores.addAll(propertiesStores);
            return this;
        }

        public CompositePropertiesStore build() {
            return new CompositePropertiesStore(propertiesStores);
        }
    }
}

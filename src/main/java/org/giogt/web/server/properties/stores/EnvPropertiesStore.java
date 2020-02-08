package org.giogt.web.server.properties.stores;

import org.giogt.web.server.properties.PropertiesStore;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A properties store retrieving properties from the OS environment variables.
 */
public class EnvPropertiesStore implements PropertiesStore {

    private final String prefix;
    private final boolean convertKeysFormat;

    EnvPropertiesStore(
            String prefix,
            boolean convertKeysFormat) {

        this.prefix = prefix;
        this.convertKeysFormat = convertKeysFormat;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getProperty(String key) {
        key = buildKeyWithPrefix(key);
        key = convertKeyFormat(key);

        return System.getenv(key);
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> properties = System.getenv();
        if (prefix != null) {
            properties = properties.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(prefix))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return properties;
    }

    @Override
    public void refresh() {
        // environment properties are always up to date => no need to refresh them
    }

    String buildKeyWithPrefix(String key) {
        if (prefix != null) {
            key = prefix + key;
        }
        return key;
    }

    String convertKeyFormat(String key) {
        if (convertKeysFormat) {
            key = key.toUpperCase();
            key = key.replaceAll("\\.|-", "_");
        }
        return key;
    }

    public static EnvPropertiesStore.Builder builder() {
        return new EnvPropertiesStore.Builder();
    }

    public static class Builder {
        private String prefix;
        private boolean convertKeysFormat = false;

        /**
         * Specifies a prefix for the keys
         *
         * <p>Default: <tt>null</tt></p>
         *
         * @param prefix
         * @return
         */
        public EnvPropertiesStore.Builder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * If true, the keys will be automatically converted to the environment
         * variable format:
         *
         * <ul>
         *     <li>capital case</li>
         *     <li>underscore to separate words</li>
         * </ul>
         *
         * <p>Default: false</p>
         *
         * @param convertKeysFormat
         * @return
         */
        public EnvPropertiesStore.Builder convertKeysFormat(
                boolean convertKeysFormat) {

            this.convertKeysFormat = convertKeysFormat;
            return this;
        }

        public EnvPropertiesStore build() {
            return new EnvPropertiesStore(prefix, convertKeysFormat);
        }
    }
}

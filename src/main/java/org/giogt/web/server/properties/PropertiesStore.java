package org.giogt.web.server.properties;

import java.util.Map;

public interface PropertiesStore {

    /**
     * Gets the value associated to the property with the specified key.
     *
     * @param key
     * @return
     */
    String getProperty(String key);

    /**
     * Returns a map containing all properties with their value.
     *
     * @return
     */
    Map<String, String> getProperties();

    void refresh();

}

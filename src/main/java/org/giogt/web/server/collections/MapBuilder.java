package org.giogt.web.server.collections;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {
    private Map<K, V> map = new HashMap<>();

    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>();
    }

    private MapBuilder() {
    }

    public MapBuilder<K, V> entry(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> entries(Map<K, V> entries) {
        map.putAll(entries);
        return this;
    }

    public Map<K, V> build() {
        return map;
    }
}

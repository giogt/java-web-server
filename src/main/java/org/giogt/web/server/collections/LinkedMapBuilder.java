package org.giogt.web.server.collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedMapBuilder<K, V> {
    private Map<K, V> map = new LinkedHashMap<>();

    public static <K, V> LinkedMapBuilder<K, V> builder() {
        return new LinkedMapBuilder<>();
    }

    private LinkedMapBuilder() {
    }

    public LinkedMapBuilder<K, V> entry(K key, V value) {
        map.put(key, value);
        return this;
    }

    public LinkedMapBuilder<K, V> entries(Map<K, V> entries) {
        map.putAll(entries);
        return this;
    }

    public Map<K, V> build() {
        return map;
    }
}

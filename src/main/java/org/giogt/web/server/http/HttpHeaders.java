package org.giogt.web.server.http;

import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class describing HTTP headers.
 * <p>
 * An HTTP header is basically a map, where the key is a string and the value
 * is a list of strings.
 * <p>
 * This class offers some convenient methods to add and get headers.
 */
@ToString(onlyExplicitlyIncluded = true)
public class HttpHeaders {

    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";

    @ToString.Include
    private LinkedHashMap<String, List<String>> headers;

    public static HttpHeaders newHeaders() {
        return new HttpHeaders();
    }

    private HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void addValue(String name, String value) {
        List<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>());
        values.add(value);
    }

    public void addValues(String name, String... values) {
        for (String value : values) {
            addValue(name, value);
        }
    }

    public List<String> getValues(String name) {
        return headers.get(name);
    }

    public String getFirstValue(String name) {
        List<String> values = getValues(name);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    public LinkedHashMap<String, List<String>> map() {
        return headers;
    }

    public int size() {
        return headers.size();
    }

    public Iterator<Map.Entry<String, List<String>>> iterator() {
        return headers.entrySet().iterator();
    }
}

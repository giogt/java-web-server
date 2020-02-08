package org.giogt.web.server.http.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class HttpHeader {
    private String name;

    @Singular
    private List<String> values;
}

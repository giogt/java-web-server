package org.giogt.web.server.http.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@Data
@Builder
public class HttpResponse {
    private HttpStatus status;
    @Singular
    private List<HttpHeader> headers;
    @ToString.Exclude
    private byte[] body;
}

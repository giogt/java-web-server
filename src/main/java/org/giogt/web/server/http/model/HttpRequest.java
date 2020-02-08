package org.giogt.web.server.http.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.giogt.web.server.http.HttpHeaders;

import java.net.URI;

@Data
@Builder
public class HttpRequest {
    private HttpMethod method;
    private URI target;
    private HttpVersion version;
    private HttpHeaders headers;
    @ToString.Exclude
    private byte[] body;
}

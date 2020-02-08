package org.giogt.web.server.http.model;

import lombok.Builder;
import lombok.Data;

import java.net.URI;

@Data
@Builder
public class RequestLine {
    private HttpMethod method;
    private URI target;
    private HttpVersion version;
}

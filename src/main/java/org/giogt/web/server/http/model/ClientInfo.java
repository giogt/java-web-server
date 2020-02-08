package org.giogt.web.server.http.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientInfo {
    private String ipAddress;
    private Integer port;
}

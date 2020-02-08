package org.giogt.web.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebServerInfo {
    private String name;
    private String version;
}

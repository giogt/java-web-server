package org.giogt.web.server;

import lombok.Builder;
import lombok.Data;
import org.giogt.web.server.config.WebServerConfig;

@Data
@Builder
public class WebServerContext {
    private WebServerConfig config;
}

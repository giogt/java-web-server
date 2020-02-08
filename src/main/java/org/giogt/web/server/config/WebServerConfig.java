package org.giogt.web.server.config;

import lombok.Data;
import org.giogt.web.server.properties.Property;

import java.net.InetAddress;
import java.nio.file.Path;

@Data
public class WebServerConfig {

    @Property(key = "address", defaultValue = "0.0.0.0")
    private InetAddress address;

    @Property(key = "port", defaultValue = "8080")
    private Integer port;

    @Property(key = "nThreads", defaultValue = "50")
    private Integer nThreads;

    @Property(key = "incomingConnectionsQueueSize", defaultValue = "50")
    private Integer incomingConnectionsQueueSize;

    @Property(key = "forceShutdownTimeoutSeconds", defaultValue = "15")
    private Integer forceShutdownTimeoutSeconds;

    @Property(key = "rootDirPath", defaultValue = "root/main")
    private Path rootDirPath;
}

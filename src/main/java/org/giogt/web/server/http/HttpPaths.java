package org.giogt.web.server.http;

import org.giogt.web.server.config.WebServerConfig;

import java.net.URI;
import java.nio.file.Path;

public class HttpPaths {

    public static Path getTargetFullPath(WebServerConfig config, URI target) {
        Path rootDirPath = config.getRootDirPath();
        return Path.of(rootDirPath.toString(), target.getPath());
    }

    // prevent instantiation
    private HttpPaths() {
    }
}

package org.giogt.web.server;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownHook extends Thread {

    private final WebServer webServer;

    public ShutdownHook(WebServer webServer) {
        this.webServer = webServer;
    }

    @Override
    public void run() {
        log.info("SIGTERM received: shutting down server");
        webServer.shutdown();
    }
}

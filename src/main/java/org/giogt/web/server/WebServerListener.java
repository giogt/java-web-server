package org.giogt.web.server;

public interface WebServerListener {
    void onWebServerStateChanged(WebServerState state, String message);
}

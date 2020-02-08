package org.giogt.web.server.http.model;

public enum HttpVersion {
    VERSION_1_0("HTTP/1.0"),
    VERSION_1_1("HTTP/1.1");

    private final String versionString;

    HttpVersion(String versionString) {
        this.versionString = versionString;
    }

    public String getVersionString() {
        return versionString;
    }
}

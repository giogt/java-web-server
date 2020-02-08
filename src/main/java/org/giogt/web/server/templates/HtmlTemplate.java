package org.giogt.web.server.templates;

public enum HtmlTemplate {
    DIR("dir.html"),
    ERROR("error.html");

    private final String path;

    HtmlTemplate(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

package org.giogt.web.server.templates.model;

import lombok.Builder;
import lombok.Data;
import org.giogt.web.server.WebServerInfo;
import org.giogt.web.server.http.model.FileElement;

import java.util.List;

@Data
@Builder
public class HtmlTemplateDirModel {
    private String targetUri;
    private List<FileElement> files;
    private WebServerInfo webServerInfo;
}

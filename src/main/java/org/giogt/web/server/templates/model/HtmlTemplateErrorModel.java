package org.giogt.web.server.templates.model;

import lombok.Builder;
import lombok.Data;
import org.giogt.web.server.WebServerInfo;
import org.giogt.web.server.http.model.HttpStatus;

@Data
@Builder
public class HtmlTemplateErrorModel {
    private HttpStatus status;
    private String message;
    private WebServerInfo webServerInfo;
}

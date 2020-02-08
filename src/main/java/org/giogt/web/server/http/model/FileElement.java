package org.giogt.web.server.http.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileElement {
    private String targetUri;
    private String name;
    private Boolean isDirectory;
}

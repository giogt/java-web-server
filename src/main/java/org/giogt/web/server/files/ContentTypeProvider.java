package org.giogt.web.server.files;

import javax.activation.MimetypesFileTypeMap;
import java.nio.file.Path;

public class ContentTypeProvider {
    private final MimetypesFileTypeMap mimetypesFileTypeMap;

    public ContentTypeProvider() {
        mimetypesFileTypeMap = new MimetypesFileTypeMap();
    }

    public String fromPath(Path path) {
        return mimetypesFileTypeMap.getContentType(
                path.getFileName().toString());
    }
}

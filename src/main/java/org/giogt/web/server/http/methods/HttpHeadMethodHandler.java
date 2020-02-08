package org.giogt.web.server.http.methods;

import org.giogt.web.server.WebServerContext;
import org.giogt.web.server.files.ContentTypeProvider;
import org.giogt.web.server.http.HttpHeaders;
import org.giogt.web.server.http.HttpPaths;
import org.giogt.web.server.http.HttpResponses;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerException;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerIOException;
import org.giogt.web.server.http.model.HttpHeader;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class HttpHeadMethodHandler implements HttpMethodHandler {

    private final WebServerContext context;
    private final ContentTypeProvider contentTypeProvider;

    public HttpHeadMethodHandler(WebServerContext context) {
        this.context = context;
        this.contentTypeProvider = new ContentTypeProvider();
    }

    @Override
    public HttpMethod handledMethod() {
        return HttpMethod.HEAD;
    }

    @Override
    public HttpResponse handle(HttpRequest request)
            throws HttpMethodHandlerException {

        URI target = request.getTarget();
        Path path = HttpPaths.getTargetFullPath(context.getConfig(), target);

        HttpResponse response;
        if (!Files.exists(path)) {
            response = HttpResponses.notFound(target);
        } else {
            List<HttpHeader> headers = Arrays.asList(
                    HttpHeader.builder()
                            .name(HttpHeaders.CONTENT_LENGTH)
                            .value(String.valueOf(getFileSize(path)))
                            .build(),
                    HttpHeader.builder()
                            .name(HttpHeaders.CONTENT_TYPE)
                            .value(contentTypeProvider.fromPath(path))
                            .build());
            response = HttpResponses.ok(headers);
        }
        return response;
    }

    long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw HttpMethodHandlerIOException.forMethod(handledMethod(), e);
        }
    }
}

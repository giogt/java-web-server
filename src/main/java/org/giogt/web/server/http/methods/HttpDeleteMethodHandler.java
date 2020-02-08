package org.giogt.web.server.http.methods;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.giogt.web.server.WebServerContext;
import org.giogt.web.server.http.HttpPaths;
import org.giogt.web.server.http.HttpResponses;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerException;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerIOException;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpResponse;

public class HttpDeleteMethodHandler implements HttpMethodHandler {

    private final WebServerContext context;

    public HttpDeleteMethodHandler(WebServerContext context) {
        this.context = context;
    }

    @Override
    public HttpMethod handledMethod() {
        return HttpMethod.DELETE;
    }

    @Override
    public HttpResponse handle(HttpRequest request)
            throws HttpMethodHandlerException {

        Path path = HttpPaths.getTargetFullPath(context.getConfig(), request.getTarget());
        delete(path);

        return HttpResponses.noContent();
    }

    boolean delete(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw HttpMethodHandlerIOException.forMethod(handledMethod(), e);
        }
    }
}

package org.giogt.web.server.http.methods;

import org.giogt.web.server.http.exceptions.HttpMethodHandlerException;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpResponse;

public interface HttpMethodHandler {
    HttpMethod handledMethod();

    HttpResponse handle(HttpRequest request) throws HttpMethodHandlerException;
}

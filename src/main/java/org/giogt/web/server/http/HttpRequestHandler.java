package org.giogt.web.server.http;

import org.giogt.web.server.WebServerContext;
import org.giogt.web.server.http.methods.HttpDeleteMethodHandler;
import org.giogt.web.server.http.methods.HttpGetMethodHandler;
import org.giogt.web.server.http.methods.HttpHeadMethodHandler;
import org.giogt.web.server.http.methods.HttpMethodHandler;
import org.giogt.web.server.http.methods.HttpPutMethodHandler;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpResponse;

public class HttpRequestHandler {

    private final WebServerContext context;

    public HttpRequestHandler(WebServerContext context) {
        this.context = context;
    }

    public HttpResponse handle(HttpRequest request) {
        HttpMethod method = request.getMethod();

        HttpMethodHandler handler = null;
        if (method == HttpMethod.GET) {
            handler = new HttpGetMethodHandler(context);
        } else if (method == HttpMethod.HEAD) {
            handler = new HttpHeadMethodHandler(context);
        } else if (method == HttpMethod.PUT) {
            handler = new HttpPutMethodHandler(context);
        } else if (method == HttpMethod.DELETE) {
            handler = new HttpDeleteMethodHandler(context);
        }

        return handler != null
                ? handler.handle(request)
                : HttpResponses.notImplemented(
                String.format(
                        "Method %s is not implemented by this server",
                        method));
    }
}

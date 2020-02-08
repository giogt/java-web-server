package org.giogt.web.server.http;

import lombok.extern.slf4j.Slf4j;
import org.giogt.web.server.WebServerContext;
import org.giogt.web.server.http.exceptions.HttpClientDisconnectedException;
import org.giogt.web.server.http.exceptions.HttpRequestHeadersTooLargeException;
import org.giogt.web.server.http.exceptions.HttpRequestParseException;
import org.giogt.web.server.http.exceptions.HttpRequestPayloadTooLargeException;
import org.giogt.web.server.http.exceptions.HttpVersionNotSupportedException;
import org.giogt.web.server.http.model.ClientInfo;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpResponse;

import java.net.Socket;
import java.util.concurrent.Callable;

@Slf4j
public class HttpConnectionHandler implements Callable<Void> {

    private final WebServerContext context;
    private final Socket clientSocket;
    private final ClientInfo clientInfo;

    public HttpConnectionHandler(
            WebServerContext context,
            Socket clientSocket,
            ClientInfo clientInfo) {

        this.context = context;
        this.clientSocket = clientSocket;
        this.clientInfo = clientInfo;
    }

    @Override
    public Void call() throws Exception {
        try {
            handleConnection();
        } catch (Throwable t) {
            log.error("error occurred while handling HTTP connection", t);
            throw t;
        }
        return null;
    }

    public void handleConnection() throws Exception {
        log.info("started handling client connection [clientInfo={}]", clientInfo);
        HttpResponse response;

        try {
            HttpRequestParser requestParser = new HttpRequestParser(clientInfo);
            HttpRequestHandler requestHandler = new HttpRequestHandler(context);
            HttpRequest request = requestParser.parse(clientSocket.getInputStream());
            response = requestHandler.handle(request);
        } catch (HttpClientDisconnectedException e) {
            log.info("client disconnected while processing request [clientInfo={}]", clientInfo);
            response = null; // no response => client is not there anymore
        } catch (HttpRequestPayloadTooLargeException e) {
            log.info("request payload is too large [clientInfo{}]", clientInfo, e);
            response = HttpResponses.payloadTooLarge(e);
        } catch (HttpRequestHeadersTooLargeException e) {
            log.info("request headers are too large [clientInfo{}]", clientInfo, e);
            response = HttpResponses.requestHeaderFieldsTooLarge(e);
        } catch (HttpRequestParseException e) {
            log.info("client request cannot be parsed [clientInfo{}]", clientInfo, e);
            response = HttpResponses.badRequest(e);
        } catch (HttpVersionNotSupportedException e) {
            log.info("requested HTTP version is not supported [clientInfo={}]", clientInfo, e);
            response = HttpResponses.versionNotSupported(e);
        } catch (Throwable t) {
            // if the exception is not handled above, it is an internal server error
            response = HttpResponses.internalServerError(t);
        }

        if (response != null) {
            HttpResponseWriter responseWriter = new HttpResponseWriter();
            responseWriter.write(clientSocket.getOutputStream(), response);
        }

        log.debug("closing connection with client ... [clientInfo={}]", clientInfo);
        clientSocket.close();
        log.info("connection with client closed [clientInfo={}]", clientInfo);
    }
}

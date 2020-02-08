package org.giogt.web.server.http;

import org.giogt.web.server.Constants;
import org.giogt.web.server.Exceptions;
import org.giogt.web.server.WebServerInfo;
import org.giogt.web.server.http.model.FileElement;
import org.giogt.web.server.http.model.HttpHeader;
import org.giogt.web.server.http.model.HttpResponse;
import org.giogt.web.server.http.model.HttpStatus;
import org.giogt.web.server.templates.HtmlTemplates;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Collection;
import java.util.List;

public class HttpResponses {

    public static HttpResponse ok(Collection<HttpHeader> headers) {
        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .headers(headers)
                .build();
    }

    public static HttpResponse ok(Collection<HttpHeader> headers, byte[] body) {
        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .headers(headers)
                .body(body)
                .build();
    }

    public static HttpResponse noContent() {
       return HttpResponse.builder()
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    public static HttpResponse badRequest(Throwable t) {
        return htmlError(
                HttpStatus.BAD_REQUEST,
                t);
    }

    public static HttpResponse notFound(URI target) {
        return htmlError(
                HttpStatus.NOT_FOUND,
                String.format("The requested URI %s was not found on this server", target));
    }

    public static HttpResponse payloadTooLarge(Throwable t) {
        return htmlError(
                HttpStatus.PAYLOAD_TOO_LARGE,
                t);
    }

    public static HttpResponse requestHeaderFieldsTooLarge(Throwable t) {
        return htmlError(
                HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE,
                t);
    }

    public static HttpResponse internalServerError(Throwable t) {
        return htmlError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                t,
                true);
    }

    public static HttpResponse notImplemented(String message) {
        return htmlError(
                HttpStatus.NOT_IMPLEMENTED,
                message);
    }

    public static HttpResponse versionNotSupported(Throwable t) {
        return htmlError(
                HttpStatus.VERSION_NOT_SUPPORTED,
                t);
    }

    public static HttpResponse directoryListing(
            String targetUri,
            List<FileElement> files) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                baos, Constants.CHARSET));
        HtmlTemplates.INSTANCE.processDirPage(
                targetUri,
                files,
                WebServerInfo.builder()
                        .name(Constants.NAME)
                        .version(Constants.VERSION)
                        .build(),
                writer);

        return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(baos.toByteArray())
                .build();
    }

    static HttpResponse htmlError(
            HttpStatus status,
            Throwable t) {

        return htmlError(status, t, false);
    }

    static HttpResponse htmlError(
            HttpStatus status,
            Throwable t,
            boolean showStackTrace) {

        return htmlError(status, toErrorMessage(t, showStackTrace));
    }

    static HttpResponse htmlError(
            HttpStatus status,
            String message) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                baos, Constants.CHARSET));
        HtmlTemplates.INSTANCE.processErrorPage(
                status,
                message,
                WebServerInfo.builder()
                        .name(Constants.NAME)
                        .version(Constants.VERSION)
                        .build(),
                writer);

        return HttpResponse.builder()
                .status(status)
                .body(baos.toByteArray())
                .build();
    }

    static String toErrorMessage(Throwable t, boolean showStackTrace) {
        StringBuilder messageBuilder = new StringBuilder()
                .append(t.getMessage());
        if (showStackTrace) {
            messageBuilder.append(Exceptions.stackTraceString(t));
        }
        return messageBuilder.toString();
    }

    // prevent instantiation
    private HttpResponses() {
    }
}

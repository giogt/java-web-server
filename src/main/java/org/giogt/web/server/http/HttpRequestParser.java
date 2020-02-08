package org.giogt.web.server.http;

import lombok.extern.slf4j.Slf4j;
import org.giogt.web.server.http.exceptions.HttpClientDisconnectedException;
import org.giogt.web.server.http.exceptions.HttpRequestHeadersTooLargeException;
import org.giogt.web.server.http.exceptions.HttpRequestParseException;
import org.giogt.web.server.http.exceptions.HttpRequestPayloadTooLargeException;
import org.giogt.web.server.http.exceptions.HttpVersionNotSupportedException;
import org.giogt.web.server.http.model.ClientInfo;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpVersion;
import org.giogt.web.server.http.model.RequestLine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@Slf4j
public class HttpRequestParser {
    public static final int HEADERS_LIMIT = 1024 * 16; // 16K
    public static final int PAYLOAD_LIMIT = 1024 * 1024 * 64; // 64 MB

    private final ClientInfo clientInfo;
    private final Pattern requestSplitPattern;

    public HttpRequestParser(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
        this.requestSplitPattern = Pattern.compile("(?U)\\s");
    }

    public HttpRequest parse(InputStream inputStream) throws IOException {
        HttpRequestReader reader = new HttpRequestReader(new BufferedInputStream(inputStream));

        RequestLine requestLine = parseRequestLine(reader);
        log.debug("request line parsed [clientInfo={}]: {}", clientInfo, requestLine);

        HttpHeaders headers = parseHeaders(reader);
        log.debug("headers parsed [clientInfo={}]: {}", clientInfo, headers);

        byte[] body = parseBody(headers, reader);
        if (body != null) {
            log.debug("body parsed [clientInfo={}]: bodyLength={}", clientInfo, body.length);
        }

        return HttpRequest.builder()
                .method(requestLine.getMethod())
                .target(requestLine.getTarget())
                .version(requestLine.getVersion())
                .headers(headers)
                .body(body)
                .build();
    }

    RequestLine parseRequestLine(HttpRequestReader reader) throws IOException {
        String line;

        // read until the stream is finished or the first non-empty line is reached
        while ((line = reader.readLine()) != null) {
            if (!isEmpty(line)) {
                /*
                 * According to HTTP/1.1 spec, empty lines received where a
                 * Request-Line is expected should be ignored.
                 */
                break;
            }
        }

        if (line == null) {
            // client disconnected before sending the request line
            throw HttpClientDisconnectedException.forClient(clientInfo);
        }
        // request line available to parse
        String[] requestParts = requestSplitPattern.split(line);
        if (requestParts.length != 3) {
            throw HttpRequestParseException.createNew("request first line must be composed of 3 parts: method, target and version");
        }

        return RequestLine.builder()
                .method(parseMethod(requestParts[0]))
                .target(parseUri(requestParts[1]))
                .version(parseVersion(requestParts[2]))
                .build();
    }

    HttpHeaders parseHeaders(HttpRequestReader reader) throws IOException {
        int headersSize = 0;

        HttpHeaders headers = HttpHeaders.newHeaders();

        String line;
        while ((line = reader.readLine()) != null) {
            headersSize += line.length();
            if (headersSize > HEADERS_LIMIT) {
                throw HttpRequestHeadersTooLargeException.forLimit(HEADERS_LIMIT);
            }

            // checking for empty line
            if (isEmpty(line)) {
                break;
            }
            addHeader(headers, line);
        }

        if (line == null) {
            // client disconnected before sending the empty line
            throw HttpClientDisconnectedException.forClient(clientInfo);
        }

        return headers;
    }

    HttpMethod parseMethod(String methodString) {
        try {
            return HttpMethod.valueOf(methodString);
        } catch (IllegalArgumentException e) {
            throw HttpRequestParseException.createNew(
                    String.format("illegal HTTP method: %s", methodString),
                    e);
        }
    }

    URI parseUri(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            throw HttpRequestParseException.createNew(
                    String.format("illegal URI string: %s", uriString),
                    e);
        }
    }

    HttpVersion parseVersion(String versionString) {
        if (HttpVersion.VERSION_1_0.getVersionString().equals(versionString)) {
            return HttpVersion.VERSION_1_0;
        } else if (HttpVersion.VERSION_1_1.getVersionString().equals(versionString)) {
            return HttpVersion.VERSION_1_1;
        } else {
            throw HttpVersionNotSupportedException.forVersionString(versionString);
        }
    }

    void addHeader(HttpHeaders headers, String line) {
        String[] nameAndValues = line.split(":", 2);
        if (nameAndValues.length != 2) {
            throw HttpRequestParseException.createNew(
                    String.format("illegal header string: %s", line));
        }
        String key = nameAndValues[0].trim();
        String valuesString = nameAndValues[1].trim();
        String[] values = valuesString.split(",");

        headers.addValues(key, values);
    }

    byte[] parseBody(HttpHeaders headers, HttpRequestReader reader) throws IOException {
        String contentLengthString = headers.getFirstValue(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthString == null || contentLengthString.equals("")) {
            return null;
        }

        int contentLength;
        try {
            contentLength = Integer.parseInt(contentLengthString);
        } catch (NumberFormatException e) {
            throw HttpRequestParseException.createNew(
                    "Cannot parse content length header value to integer: " + contentLengthString,
                    e);
        }
        if (contentLength > PAYLOAD_LIMIT) {
            throw HttpRequestPayloadTooLargeException.forLimit(PAYLOAD_LIMIT);
        }

        return reader.readBytes(contentLength);
    }

    boolean isEmpty(String line) {
        return line.equals("");
    }
}

package org.giogt.web.server.http;

import static org.giogt.web.server.test.TestInputStreams.toInputStream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.giogt.web.server.collections.LinkedMapBuilder;
import org.giogt.web.server.http.exceptions.HttpClientDisconnectedException;
import org.giogt.web.server.http.exceptions.HttpRequestHeadersTooLargeException;
import org.giogt.web.server.http.exceptions.HttpRequestParseException;
import org.giogt.web.server.http.exceptions.HttpRequestPayloadTooLargeException;
import org.giogt.web.server.http.model.ClientInfo;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpVersion;
import org.junit.jupiter.api.Test;

public class HttpRequestParserTest {

    @Test
    public void parse_whenInputStreamIsEmpty_mustThrowClientDisconnectedException() {
        HttpRequestParser requestParser = createInstance();
        InputStream inputStream = toInputStream();
        assertThrows(
                HttpClientDisconnectedException.class,
                () -> requestParser.parse(inputStream));
    }

    @Test
    public void parse_whenRequestIsValidGetWithoutHeaders_mustParseTheRequest()
            throws IOException {

        HttpRequestParser requestParser = createInstance();

        InputStream inputStream = toInputStream(
                "GET /test.txt HTTP/1.1",
                "");
        HttpRequest request = requestParser.parse(inputStream);
        assertThat(request, is(notNullValue()));
        assertThat(request.getMethod(), is(HttpMethod.GET));
        assertThat(request.getTarget(), is(URI.create("/test.txt")));
        assertThat(request.getVersion(), is(HttpVersion.VERSION_1_1));
    }

    @Test
    public void parse_whenRequestIsValidGetWithHeaders_mustParseTheRequest()
            throws IOException {

        HttpRequestParser requestParser = createInstance();

        InputStream inputStream = toInputStream(
                "GET /test.txt HTTP/1.1",
                "Accept: text/html,application/xhtml+xml",
                "Accept-Language: en-us,en",
                "Accept-Charset: utf-8,ISO-8859-1",
                "");

        HttpRequest request = requestParser.parse(inputStream);
        assertThat(request, is(notNullValue()));
        assertThat(request.getMethod(), is(HttpMethod.GET));
        assertThat(request.getTarget(), is(URI.create("/test.txt")));
        assertThat(request.getVersion(), is(HttpVersion.VERSION_1_1));

        HttpHeaders headers = request.getHeaders();
        assertThat(headers.size(), is(3));
        assertThat(headers.map(), is(LinkedMapBuilder.builder()
                .entry("Accept", Arrays.asList("text/html", "application/xhtml+xml"))
                .entry("Accept-Language", Arrays.asList("en-us", "en"))
                .entry("Accept-Charset", Arrays.asList("utf-8", "ISO-8859-1"))
                .build()));
    }

    @Test
    public void parse_whenRequestHasAnIllegalMethod_mustThrowParseException() {
        HttpRequestParser requestParser = createInstance();
        InputStream inputStream = toInputStream("ILLEGAL_METHOD /test.txt HTTP/1.1");
        Throwable e = assertThrows(
                HttpRequestParseException.class,
                () -> requestParser.parse(inputStream));
        assertThat(e.getMessage(), stringContainsInOrder("illegal HTTP method"));
    }

    @Test
    public void parse_whenRequestHasAnIllegalUri_mustThrowParseException() {
        HttpRequestParser requestParser = createInstance();
        InputStream inputStream = toInputStream("GET <%illegal#uri%> HTTP/1.1");
        Throwable e = assertThrows(
                HttpRequestParseException.class,
                () -> requestParser.parse(inputStream));
        assertThat(e.getMessage(), stringContainsInOrder("illegal URI string"));
    }

    @Test
    public void parse_whenHeadersAreTooLarge_mustThrowHttpRequestHeadersTooLargeException() {
        HttpRequestParser requestParser = createInstance();
        StringBuilder requestBuilder = new StringBuilder("GET /test.txt HTTP/1.1\n");

        int cntr = 0;
        int headersLength = 0;
        while (headersLength < HttpRequestParser.HEADERS_LIMIT + 20) {
            String header = String.format("Header: value%s", cntr++);
            requestBuilder.append(header).append("\n");
            headersLength += header.length();
        }
        InputStream inputStream = toInputStream(requestBuilder.toString());
        assertThrows(
                HttpRequestHeadersTooLargeException.class,
                () -> requestParser.parse(inputStream));
    }

    @Test
    public void parse_whenPayloadIsTooLarge_mustThrowHttpRequestPayloadTooLargeException() {
        HttpRequestParser requestParser = createInstance();
        InputStream inputStream = toInputStream(
                "PUT /test.txt HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: " + String.valueOf(HttpRequestParser.PAYLOAD_LIMIT + 20),
                "");

        assertThrows(
                HttpRequestPayloadTooLargeException.class,
                () -> requestParser.parse(inputStream));
    }

    HttpRequestParser createInstance() {
        return new HttpRequestParser(
                ClientInfo.builder()
                        .ipAddress("0.0.0.0")
                        .port(1234)
                        .build());
    }
}

package org.giogt.web.server.http;

import org.giogt.web.server.http.exceptions.HttpRequestLineTooLargeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.giogt.web.server.test.TestInputStreams.emptyInputStream;
import static org.giogt.web.server.test.TestInputStreams.toInputStream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpRequestReaderTest {

    @Test
    public void readLine_whenStreamEndIsReached_mustReturnNull()
            throws IOException {

        InputStream inputStream = emptyInputStream();
        HttpRequestReader reader = createReader(inputStream);
        assertThat(reader.readLine(), is(nullValue()));
    }

    @Test
    public void readLine_mustReadALineEndingWithLF()
            throws IOException {

        InputStream inputStream = toInputStream("this is a line\nthis is another line");
        HttpRequestReader reader = createReader(inputStream);
        assertThat(reader.readLine(), is("this is a line"));
    }

    @Test
    public void readLine_mustReadALineEndingWithCRLF()
            throws IOException {

        InputStream inputStream = toInputStream("this is a line\r\nthis is another line");
        HttpRequestReader reader = createReader(inputStream);
        assertThat(reader.readLine(), is("this is a line"));
    }

    @Test
    public void readLine_whenLineIsLongerThanLimit_mustThrowHttpRequestLineTooLargeException() {
        InputStream inputStream = toInputStream(generateStringBiggerThanLimit());
        HttpRequestReader reader = createReader(inputStream);
        assertThrows(HttpRequestLineTooLargeException.class, reader::readLine);
    }

    private HttpRequestReader createReader(InputStream inputStream) {
        return new HttpRequestReader(inputStream);
    }

    private String generateStringBiggerThanLimit() {
        return "A".repeat(HttpRequestReader.LINE_LIMIT + 20);
    }
}

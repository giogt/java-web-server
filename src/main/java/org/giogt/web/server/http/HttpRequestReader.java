package org.giogt.web.server.http;

import org.giogt.web.server.http.exceptions.HttpRequestLineTooLargeException;
import org.giogt.web.server.http.exceptions.HttpRequestParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * A reader specialised to read HTTP requests.
 * <p>
 * In HTTP requests, the request line and the headers consists of US-ASCII
 * characters, with a CRLF line separator.
 * The body of the request, instead, is an octect, therefore it must be read
 * as a stream of bytes.
 * <p>
 * In Java, you cannot safely create a reader wrapping an input stream and
 * then use the reader for text and the input stream for bytes, since the
 * reader might read some data ahead, creating unpredictable behaviour on the
 * underlying input stream when accessed directly.
 * <p>
 * This class provides a reader that can safely:
 * <ul>
 * <li>read US-ASCII lines, with CRLF or LF line separators</li>
 * <li>read bytes</li>
 * </ul>
 * <p>
 * It can therefore be used to parse the HTTP request line, headers and body.
 */
public class HttpRequestReader {
    public static final int LINE_LIMIT = 1024 * 8; // 8K

    private final InputStream inputStream;

    public HttpRequestReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Reads a line from the input stream.
     * <p>
     * For this method, a line is a sequence of US-ASCII characters ending with
     * a CRLF or LF.
     *
     * @return
     * @throws IOException when an IO error occurs
     * @throws HttpRequestParseException when the line is longer than {@link #LINE_LIMIT}
     */
    public String readLine() throws IOException {
        StringBuilder stringBuilder = null;
        int c;
        int cntr = 0;
        while ((c = inputStream.read()) != -1) {
            if (++cntr > LINE_LIMIT) {
                throw HttpRequestLineTooLargeException.forLimit(LINE_LIMIT);
            }

            if (stringBuilder == null) {
                stringBuilder = new StringBuilder();
            }
            if (c == '\n') {
                break;
            }
            stringBuilder.append((char) c);
        }

        if (stringBuilder == null) {
            // nothing was read
            return null;
        }
        String line = stringBuilder.toString();
        // removing \r at the end of the string, if any, since the new line could be \r\n
        if (line.length() > 0 && line.charAt(line.length() - 1) == '\r') {
            line = line.substring(0, line.length() - 1);
        }
        return line;
    }

    public byte[] readBytes(int nToRead) throws IOException {
        byte[] bytes = new byte[nToRead];
        int off = 0;
        int len = nToRead;
        int nBytesRead;
        while ((nBytesRead = inputStream.read(bytes, off, len)) != -1) {
            if (nBytesRead >= len - off) {
                break;
            }
            off += nBytesRead;
            len = nToRead - off;
        }

        inputStream.mark(Integer.MAX_VALUE);
        return bytes;
    }
}

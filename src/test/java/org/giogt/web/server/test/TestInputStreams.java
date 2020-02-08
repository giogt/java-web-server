package org.giogt.web.server.test;

import org.giogt.web.server.Constants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TestInputStreams {

    public static InputStream emptyInputStream() {
        return new ByteArrayInputStream(new byte[]{});
    }

    public static InputStream toInputStream(String string) {
        return new ByteArrayInputStream(string.getBytes(Constants.CHARSET));
    }

    public static InputStream toInputStream(String... lines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            stringBuilder.append(line).append("\n");
        }
        return new ByteArrayInputStream(stringBuilder.toString().getBytes(Constants.CHARSET));
    }
}

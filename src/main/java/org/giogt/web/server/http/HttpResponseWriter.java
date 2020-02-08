package org.giogt.web.server.http;

import org.giogt.web.server.http.model.HttpHeader;
import org.giogt.web.server.http.model.HttpResponse;
import org.giogt.web.server.http.model.HttpVersion;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponseWriter {

    public void write(OutputStream outputStream, HttpResponse response) throws IOException {
        writeLine(
                outputStream,
                String.format("%s %s %s",
                        HttpVersion.VERSION_1_1.getVersionString(),
                        response.getStatus().getCode(),
                        response.getStatus().getDescription()));

        for (HttpHeader header : response.getHeaders()) {
            writeLine(outputStream, String.format("%s: %s", header.getName(), header.getValues().get(0)));
        }
        writeLine(outputStream, "");

        if (response.getBody() != null) {
            outputStream.write(response.getBody());
        }
    }

    void writeLine(OutputStream outputStream, String string) throws IOException {
        outputStream.write(string.concat("\n").getBytes(StandardCharsets.UTF_8));
    }
}

package org.giogt.web.server.http.methods;

import org.giogt.web.server.WebServerContext;
import org.giogt.web.server.http.HttpPaths;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerException;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerIOException;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpResponse;
import org.giogt.web.server.http.model.HttpStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class HttpPutMethodHandler implements HttpMethodHandler {

    private final WebServerContext context;

    public HttpPutMethodHandler(WebServerContext context) {
        this.context = context;
    }

    @Override
    public HttpMethod handledMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public HttpResponse handle(HttpRequest request)
            throws HttpMethodHandlerException {

        Path path = HttpPaths.getTargetFullPath(context.getConfig(), request.getTarget());

        try {
            writeFileContent(request.getBody(), path);
            return HttpResponse.builder()
                    .status(Files.exists(path) ? HttpStatus.NO_CONTENT : HttpStatus.CREATED)
                    .build();
        } catch (IOException e) {
            throw HttpMethodHandlerIOException.forMethod(handledMethod(), e);
        }
    }

    void writeFileContent(byte[] body, Path path) throws IOException {
        // create file if it doesn't exist
        if (Files.notExists(path)) {
            Files.createFile(path);
        }

        // create channel and acquire shared lock (for write)
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE);
        FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, false);
        try {
            int nBytesToWrite = body.length;
            while (nBytesToWrite > 0) {
                int nBytesWritten = fileChannel.write(ByteBuffer.wrap(body));
                nBytesToWrite -= nBytesWritten;
            }
        } finally {
            lock.release();
            fileChannel.close();
        }
    }
}

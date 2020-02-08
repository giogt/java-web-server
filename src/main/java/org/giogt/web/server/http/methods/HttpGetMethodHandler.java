package org.giogt.web.server.http.methods;

import org.giogt.web.server.WebServerContext;
import org.giogt.web.server.files.ContentTypeProvider;
import org.giogt.web.server.http.HttpHeaders;
import org.giogt.web.server.http.HttpPaths;
import org.giogt.web.server.http.HttpResponses;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerException;
import org.giogt.web.server.http.exceptions.HttpMethodHandlerIOException;
import org.giogt.web.server.http.model.FileElement;
import org.giogt.web.server.http.model.HttpHeader;
import org.giogt.web.server.http.model.HttpMethod;
import org.giogt.web.server.http.model.HttpRequest;
import org.giogt.web.server.http.model.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpGetMethodHandler implements HttpMethodHandler {
    private static final int BUFFER_SIZE = 1024 * 8; // 8K

    private final WebServerContext context;
    private final ContentTypeProvider contentTypeProvider;

    public HttpGetMethodHandler(WebServerContext context) {
        this.context = context;
        this.contentTypeProvider = new ContentTypeProvider();
    }

    @Override
    public HttpMethod handledMethod() {
        return HttpMethod.GET;
    }

    @Override
    public HttpResponse handle(HttpRequest request)
            throws HttpMethodHandlerException {

        URI target = request.getTarget();
        Path path = HttpPaths.getTargetFullPath(context.getConfig(), target);

        HttpResponse response;
        if (!Files.exists(path)) {
            response = HttpResponses.notFound(target);
        } else {
            if (Files.isDirectory(path)) {
                List<FileElement> files = listFiles(path, target);
                response = HttpResponses.directoryListing(
                        target.toString(),
                        files);
            } else {
                byte[] body = toBody(path);
                List<HttpHeader> headers = Arrays.asList(
                        HttpHeader.builder()
                                .name(HttpHeaders.CONTENT_LENGTH)
                                .value(String.valueOf(body.length))
                                .build(),
                        HttpHeader.builder()
                                .name(HttpHeaders.CONTENT_TYPE)
                                .value(contentTypeProvider.fromPath(path))
                                .build());

                response = HttpResponses.ok(headers, body);
            }
        }

        return response;
    }

    byte[] toBody(Path path) {
        try {
            return readFileContent(path);
        } catch (IOException e) {
            throw HttpMethodHandlerIOException.forMethod(handledMethod(), e);
        }
    }

    byte[] readFileContent(Path path) throws IOException {
        // create channel and acquire shared lock (for reading)
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (fileChannel.read(buffer) != -1) {
                buffer.flip();
                if (buffer.hasRemaining()) {
                    byte[] b = new byte[buffer.remaining()];
                    buffer.get(b);
                    contentStream.writeBytes(b);
                }
                buffer.clear();
            }
            return contentStream.toByteArray();
        }
    }

    List<FileElement> listFiles(
            Path dirPath,
            URI target) {

        try (Stream<Path> stream = Files.walk(dirPath, 1)) {
            return stream
                    .filter(p -> !p.equals(dirPath))
                    .map(p -> {
                        String fileName = p.getFileName().toString();
                        return FileElement.builder()
                                .targetUri(Path.of(target.getPath(), fileName).toString())
                                .name(fileName)
                                .isDirectory(Files.isDirectory(p))
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw HttpMethodHandlerIOException.forMethod(handledMethod(), e);
        }
    }
}

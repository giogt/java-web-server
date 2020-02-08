package org.giogt.web.server.files;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ContentTypeProviderTest {

    @Test
    public void fromFile_forTxtExtension_mustReturnPlainText() {
        ContentTypeProvider contentTypeProvider = createInstance();
        assertThat(contentTypeProvider.fromPath(Path.of("file.txt")), is("text/plain"));
        assertThat(contentTypeProvider.fromPath(Path.of("/this/is/a/file.txt")), is("text/plain"));
    }

    @Test
    public void fromFile_forJsonExtension_mustReturnApplicationJson() {
        ContentTypeProvider contentTypeProvider = createInstance();
        assertThat(contentTypeProvider.fromPath(Path.of("file.json")), is("application/json"));
        assertThat(contentTypeProvider.fromPath(Path.of("/this/is/a/file.json")), is("application/json"));
    }

    @Test
    public void fromFile_forUnknownExtension_mustReturnApplicationOctetStream() {
        ContentTypeProvider contentTypeProvider = createInstance();
        assertThat(contentTypeProvider.fromPath(Path.of("test.asdfasdfasdf")), is("application/octet-stream"));
    }

    ContentTypeProvider createInstance() {
        return new ContentTypeProvider();
    }
}

package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PathPropertySerializerTest {

    @Test
    public void fromString_whenStringValueIsNull_mustReturnNull() {
        String pathString = null;

        PathPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        Path result = serializer.fromString(context, pathString);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void fromString_whenStringValueIsAValidAbsolutePathString_mustReturnCorrespondingPath() {
        String pathString = "/a/Path/path.extension";

        PathPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        Path result = serializer.fromString(context, pathString);

        assertThat(result, is(Path.of(pathString)));
        assertThat(result.toString(), is(pathString));
    }

    @Test
    public void fromString_whenStringValueIsAValidRelativePathString_mustReturnCorrespondingPath() {
        String path = "a/Path/path.extension";

        PathPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        Path result = serializer.fromString(context, path);

        assertThat(result, is(Path.of(path)));
        assertThat(result.toString(), is(path));
    }


    @Test
    public void toString_whenValueIsNull_mustReturnNull() {
        Path value = null;

        PathPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, value);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void toString_whenValueIsAPath_mustReturnCorrespondingPathString() {
        String path = "/a/Path/path.extension";
        Path value = Path.of(path);

        PathPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, value);

        assertThat(result, is(path));

    }

    PathPropertySerializer createSerializer() {
        return new PathPropertySerializer();
    }

    MappingContext createMappingContext() {
        MappingContext context = new MappingContext();
        context.setMappingType(Path.class);
        return context;
    }
}

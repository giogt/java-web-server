package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongPropertySerializerTest {

    @Test
    public void fromString_whenStringValueIsNull_mustReturnNull() {
        String stringValue = null;

        LongPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        Long result = serializer.fromString(context, stringValue);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void fromString_whenStringValueIsAValidLongStringRepresentation_mustReturnCorrespondingLong() {
        String stringValue = "12345";
        Long expected = 12345L;

        LongPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        Long result = serializer.fromString(context, stringValue);

        assertThat(result, is(expected));
    }

    @Test
    public void fromString_whenStringValueIsANonValidLongStringRepresentation_mustThrowSerializationException() {
        String stringValue = "!?12345?!";

        LongPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        assertThrows(PropertySerializationException.class, () ->
                serializer.fromString(context, stringValue)
        );
    }


    @Test
    public void toString_whenValueIsNull_mustReturnNull() {
        Long value = null;

        LongPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, value);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void toString_whenValueIsAValidLong_mustReturnTheLongStringRepresentation() {
        Long value = 12345L;
        String expected = "12345";

        LongPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, value);

        assertThat(result, is(expected));
    }

    LongPropertySerializer createSerializer() {
        return new LongPropertySerializer();
    }

    MappingContext createMappingContext() {
        MappingContext context = new MappingContext();
        context.setMappingType(Long.class);
        return context;
    }
}

package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntegerPropertySerializerTest {

    @Test
    public void fromString_whenStringValueIsNull_mustReturnNull() {
        IntegerPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        Integer result = serializer.fromString(context, null);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void fromString_whenStringValueIsAValidIntegerStringRepresentation_mustReturnCorrespondingInteger() {
        String stringValue = "12345";
        Integer expected = 12345;

        IntegerPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        Integer result = serializer.fromString(context, stringValue);

        assertThat(result, is(expected));
    }

    @Test
    public void fromString_whenStringValueIsANonValidIntegerStringRepresentation_mustThrowSerializationException() {
        String stringValue = "!?12345?!";

        IntegerPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        assertThrows(PropertySerializationException.class, () ->
                serializer.fromString(context, stringValue)
        );
    }


    @Test
    public void toString_whenValueIsNull_mustReturnNull() {
        IntegerPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, null);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void toString_whenValueIsAValidInteger_mustReturnTheIntegerStringRepresentation() {
        Integer value = 12345;
        String expected = "12345";

        IntegerPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, value);

        assertThat(result, is(expected));
    }

    IntegerPropertySerializer createSerializer() {
        return new IntegerPropertySerializer();
    }

    MappingContext createMappingContext() {
        MappingContext context = new MappingContext();
        context.setMappingType(Integer.class);
        return context;
    }
}

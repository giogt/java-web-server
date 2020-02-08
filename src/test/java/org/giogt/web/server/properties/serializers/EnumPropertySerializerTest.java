package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnumPropertySerializerTest {

    enum TestEnum {
        FOO,
        BAR
    }

    @Test
    public void fromString_whenPropertyValueIsNull_mustReturnNull() {
        String propertyValue = null;

        EnumPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        TestEnum result = (TestEnum) iut.fromString(context, propertyValue);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void fromString_whenPropertyValueIsAValidEnumStringRepresentation_mustReturnTheEnum() {
        String propertyValue = TestEnum.FOO.toString();
        TestEnum expected = TestEnum.FOO;

        EnumPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        TestEnum result = (TestEnum) iut.fromString(context, propertyValue);

        assertThat(result, is(expected));
    }

    @Test
    public void fromString_whenPropertyValueIsANonValidEnumStringRepresentation_mustThrowSerializationException() {
        String propertyValue = "NON_VALID_ENUM_STRING_REPRESENTATION";

        EnumPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        assertThrows(PropertySerializationException.class, () ->
                iut.fromString(context, propertyValue)
        );
    }


    @Test
    public void toString_whenValueIsNull_mustReturnNull() {
        TestEnum value = null;

        EnumPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        String result = iut.toString(context, value);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void toString_whenValueIsAValidEnum_mustReturnTheEnumStringRepresentation() {
        TestEnum value = TestEnum.BAR;
        String expected = TestEnum.BAR.toString();

        EnumPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        String result = iut.toString(context, value);

        assertThat(result, is(expected));
    }

    EnumPropertySerializer createSerializer() {
        return new EnumPropertySerializer();
    }

    MappingContext createMappingContext() {
        MappingContext context = new MappingContext();
        context.setMappingType(TestEnum.class);
        return context;
    }
}

package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BooleanPropertySerializerTest {

    @Test
    public void fromString_whenPropertyValueIsNull_mustReturnNull() {
        String propertyValue = null;

        BooleanPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        Boolean result = iut.fromString(context, propertyValue);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void fromString_whenPropertyValueIsStringTrue_mustReturnTrue() {
        String propertyValue = BooleanValues.TRUE_STRING;
        Boolean expected = Boolean.TRUE;

        BooleanPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        Boolean result = iut.fromString(context, propertyValue);

        assertThat(result, is(expected));
    }

    @Test
    public void fromString_whenPropertyValueIsStringFalse_mustReturnFalse() {
        String propertyValue = BooleanValues.FALSE_STRING;
        Boolean expected = Boolean.FALSE;

        BooleanPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        Boolean result = iut.fromString(context, propertyValue);

        assertThat(result, is(expected));
    }

    @Test
    public void fromString_whenPropertyValueIsANonValidBooleanStringRepresentation_mustThrowSerializationException() {
        String propertyValue = "non-valid-boolean-representation";

        BooleanPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();

        assertThrows(PropertySerializationException.class, () ->
                iut.fromString(context, propertyValue)
        );
    }


    @Test
    public void toString_whenValueIsNull_mustReturnNull() {
        Boolean value = null;

        BooleanPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        String result = iut.toString(context, value);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void toString_whenValueIsTrue_mustReturnStringTrue() {
        Boolean value = Boolean.TRUE;
        String expected = BooleanValues.TRUE_STRING;

        BooleanPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        String result = iut.toString(context, value);

        assertThat(result, is(expected));
    }

    @Test
    public void toString_whenPropertyValueIsFalse_mustReturnStringFalse() {
        Boolean value = Boolean.FALSE;
        String expected = BooleanValues.FALSE_STRING;

        BooleanPropertySerializer iut = createSerializer();
        MappingContext context = createMappingContext();
        String result = iut.toString(context, value);

        assertThat(result, is(expected));
    }

    BooleanPropertySerializer createSerializer() {
        return new BooleanPropertySerializer();
    }

    MappingContext createMappingContext() {
        MappingContext context = new MappingContext();
        context.setMappingType(Boolean.class);
        return context;
    }
}

package org.giogt.web.server.properties;

import org.giogt.web.server.properties.serializers.InetAddressPropertySerializer;
import org.giogt.web.server.properties.serializers.IntegerPropertySerializer;
import org.giogt.web.server.properties.serializers.StringPropertySerializer;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;

public class PropertySerializersTest {

    @Test
    public void forType_mustReturnSerializersForAllRegisteredTypes() {
        PropertySerializers propertySerializers = new PropertySerializers();

        assertThat(propertySerializers.forType(InetAddress.class), isA(InetAddressPropertySerializer.class));
        assertThat(propertySerializers.forType(Integer.class), isA(IntegerPropertySerializer.class));
        assertThat(propertySerializers.forType(String.class), isA(StringPropertySerializer.class));
    }

}
package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InetAddressPropertySerializerTest {

    @Test
    public void fromString_whenStringValueIsNull_mustReturnNull() {
        String stringValue = null;

        InetAddressPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        InetAddress result = serializer.fromString(context, stringValue);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void fromString_whenStringValueIsAValidHostname_mustReturnCorrespondingInetAddress() {
        String stringValue = "localhost";
        byte[] expected = new byte[]{127, 0, 0, 1};

        InetAddressPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        InetAddress result = serializer.fromString(context, stringValue);


        assertThat(result.getAddress(), is(expected));
    }

    @Test
    public void fromString_whenStringValueIsAValidIPAddress_mustReturnCorrespondingInetAddress() {
        String stringValue = "127.0.0.1";
        byte[] expected = new byte[]{127, 0, 0, 1};

        InetAddressPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        InetAddress result = serializer.fromString(context, stringValue);


        assertThat(result.getAddress(), is(expected));
    }

    @Test
    public void fromString_whenStringValueIsTheWildcardIPAddress_mustReturnCorrespondingInetAddress() {
        String stringValue = "0.0.0.0";
        byte[] expected = new byte[]{0, 0, 0, 0};

        InetAddressPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        InetAddress result = serializer.fromString(context, stringValue);

        assertThat(result.getAddress(), is(expected));
    }

    @Test
    public void fromString_whenStringValueIsAnUnknownHostname_mustThrowPropertySerializationException() {
        String stringValue = "anUnknowHostnameWhichShouldReallyReallyNotExist.EvenThoughThereCouldBeAHostnameLikeThisInANetworkItIsVeryUnlikely.WhatAreTheOdds";

        InetAddressPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        assertThrows(PropertySerializationException.class, () ->
                serializer.fromString(context, stringValue)
        );
    }

    @Test
    public void toString_whenValueIsNull_mustReturnNull() {
        InetAddress value = null;

        InetAddressPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, value);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void toString_whenValueIsAInetAddress_mustReturnItsStringRepresentation()
            throws UnknownHostException {

        InetAddress value = InetAddress.getByName("127.0.0.1");

        InetAddressPropertySerializer serializer = createSerializer();
        MappingContext context = createMappingContext();
        String result = serializer.toString(context, value);

        assertThat(result, is("/127.0.0.1"));
    }

    InetAddressPropertySerializer createSerializer() {
        return new InetAddressPropertySerializer();
    }

    MappingContext createMappingContext() {
        MappingContext context = new MappingContext();
        context.setMappingType(InetAddress.class);
        return context;
    }
}
package org.giogt.web.server.properties.serializers;

import org.giogt.web.server.properties.MappingContext;
import org.giogt.web.server.properties.PropertySerializationException;
import org.giogt.web.server.properties.PropertySerializer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressPropertySerializer implements PropertySerializer<InetAddress> {
    @Override
    public Class<? extends InetAddress> handledType() {
        return InetAddress.class;
    }

    @Override
    public InetAddress fromString(
            MappingContext context,
            String stringValue)
            throws PropertySerializationException {

        if (stringValue == null) {
            return null;
        }

        InetAddress value;
        try {
            value = InetAddress.getByName(stringValue);
        } catch (UnknownHostException e) {
            throw new PropertySerializationException(
                    "cannot parse property value <" + stringValue + "> to a valid IP address",
                    e);
        }

        return value;
    }

    @Override
    public String toString(
            MappingContext context,
            InetAddress value)
            throws PropertySerializationException {

        if (value == null) {
            return null;
        }

        return value.toString();
    }
}

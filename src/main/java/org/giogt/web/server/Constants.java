package org.giogt.web.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {

    public static final String NAME = "giogt web-server";
    public static final String VERSION = "0.1.0";
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    // prevent instantiation
    private Constants() {
    }
}

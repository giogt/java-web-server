package org.giogt.web.server;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {

    public static String stackTraceString(Throwable t) {
        Preconditions.notNull(t, "throwable");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);

        return stringWriter.getBuffer().toString();
    }

    // prevent instantiation
    private Exceptions() {
    }
}

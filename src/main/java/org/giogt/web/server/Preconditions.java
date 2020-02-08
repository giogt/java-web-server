package org.giogt.web.server;

import java.util.function.Supplier;

public class Preconditions {

    // prevent instantiation
    private Preconditions() {
        throw new AssertionError("Class <" + Preconditions.class + "> cannot be instantiated");
    }

    public static <T> T notNull(T reference, String referenceName) {
        return notNull(reference, () -> String.format("<%s> cannot be null", referenceName));
    }

    public static <T> T notNull(
            T reference,
            Supplier<String> messageSupplier) {

        if (reference == null) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return reference;
    }


    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArgument(
            boolean expression,
            Supplier<String> messageSupplier) {

        if (!expression) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }


    public static void checkPositionIndex(int index, int size, String indexName) {
        if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException(String.format(
                    "%s (%s) must not be negative", indexName, index));
        }
        if (index > size) {
            throw new IndexOutOfBoundsException(String.format(
                    "%s (%s) must not be greater than size (%s)", indexName, index, size));
        }
    }

}

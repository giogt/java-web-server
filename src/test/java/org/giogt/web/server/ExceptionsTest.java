package org.giogt.web.server;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.fail;

public class ExceptionsTest {

    @Test
    public void stackTraceString_forAThrowable_mustExtractAStringRepresentationOfTheStacktrace() {
        try {
            alwaysFailingMethod();
            fail("this should have been unreachable code");
        } catch (Throwable t) {
            String stackTraceString = Exceptions.stackTraceString(t);
            assertThat(stackTraceString, is(notNullValue()));
            assertThat(stackTraceString, stringContainsInOrder(
                    String.format(
                            "%s$%s: I am an outer exception instance",
                            ExceptionsTest.class.getName(),
                            OuterException.class.getSimpleName()),
                    String.format(
                            "Caused by: %s$%s: I am an inner exception instance",
                            ExceptionsTest.class.getName(),
                            InnerException.class.getSimpleName())));
        }
    }

    private void alwaysFailingMethod() {
        try {
            alwaysFailingInner();
        } catch (InnerException e) {
            throw new OuterException(e);
        }
    }

    private void alwaysFailingInner() {
        throw new InnerException();
    }

    private static class OuterException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public OuterException(Throwable t) {
            super("I am an outer exception instance", t);
        }
    }

    private static class InnerException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InnerException() {
            super("I am an inner exception instance");
        }
    }

}
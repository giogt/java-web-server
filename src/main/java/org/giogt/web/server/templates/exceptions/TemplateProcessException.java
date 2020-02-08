package org.giogt.web.server.templates.exceptions;

import java.text.MessageFormat;

public class TemplateProcessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "error while processing template {0}";

    private TemplateProcessException(String message) {
        super(message);
    }

    private TemplateProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public static TemplateProcessException forTemplate(
            String template,
            Throwable t) {

        return new TemplateProcessException(
                MessageFormat.format(MESSAGE, template),
                t);
    }
}

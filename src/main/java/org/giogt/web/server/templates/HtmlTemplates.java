package org.giogt.web.server.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.giogt.web.server.WebServerInfo;
import org.giogt.web.server.http.model.FileElement;
import org.giogt.web.server.http.model.HttpStatus;
import org.giogt.web.server.templates.exceptions.TemplateProcessException;
import org.giogt.web.server.templates.model.HtmlTemplateDirModel;
import org.giogt.web.server.templates.model.HtmlTemplateErrorModel;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public enum HtmlTemplates {
    INSTANCE;

    private final Configuration templatesConfig;

    HtmlTemplates() {
        templatesConfig = new Configuration(Configuration.VERSION_2_3_29);
        templatesConfig.setClassLoaderForTemplateLoading(
                Thread.currentThread().getContextClassLoader(),
                "templates/html");
        templatesConfig.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        templatesConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        templatesConfig.setLogTemplateExceptions(false);
        templatesConfig.setFallbackOnNullLoopVariable(false);
    }

    public void processDirPage(
            String targetUri,
            List<FileElement> files,
            WebServerInfo webServerInfo,
            Writer writer) {

        HtmlTemplateDirModel model = HtmlTemplateDirModel.builder()
                .targetUri(targetUri)
                .files(files)
                .webServerInfo(webServerInfo)
                .build();
        process(HtmlTemplate.DIR, model, writer);
    }

    public void processErrorPage(
            HttpStatus status,
            String message,
            WebServerInfo webServerInfo,
            Writer writer) {

        HtmlTemplateErrorModel model = HtmlTemplateErrorModel.builder()
                .status(status)
                .message(message)
                .webServerInfo(webServerInfo)
                .build();
        process(HtmlTemplate.ERROR, model, writer);
    }

    public void process(
            HtmlTemplate htmlTemplate,
            Object dataModel,
            Writer writer) {

        String templateName = htmlTemplate.getPath();
        try {
            Template template = templatesConfig.getTemplate(templateName);
            template.process(dataModel, writer);
        } catch (IOException | TemplateException e) {
            throw TemplateProcessException.forTemplate(templateName, e);
        }
    }
}

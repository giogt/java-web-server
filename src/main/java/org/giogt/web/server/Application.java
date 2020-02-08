package org.giogt.web.server;

import org.giogt.web.server.config.ConfigProvider;
import org.giogt.web.server.config.WebServerConfig;
import org.giogt.web.server.properties.PropertiesStore;
import org.giogt.web.server.properties.PropertySerializers;
import org.giogt.web.server.properties.stores.CompositePropertiesStore;
import org.giogt.web.server.properties.stores.EnvPropertiesStore;
import org.giogt.web.server.properties.stores.JavaPropertiesStore;

public class Application {

    public static void main(String[] args) throws Exception {
        PropertySerializers propertySerializers = new PropertySerializers();
        PropertiesStore propertiesStore = buildPropertiesStore();

        ConfigProvider configProvider = new ConfigProvider(
                propertySerializers,
                propertiesStore
        );
        WebServerConfig config = configProvider.getConfig();

        WebServerContext context = WebServerContext.builder()
                .config(config)
                .build();

        WebServer webServer = new WebServer(context);
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(webServer));

        webServer.bind();
        webServer.run();
    }

    private static PropertiesStore buildPropertiesStore() {
        JavaPropertiesStore javaPropertiesStore = JavaPropertiesStore.builder()
                .withPrefix("org.giogt.web.server.")
                .build();
        EnvPropertiesStore envPropertiesStore = EnvPropertiesStore.builder()
                .withPrefix("ORG_GIOGT_WEB_SERVER_")
                .convertKeysFormat(true)
                .build();
        return CompositePropertiesStore.builder()
                .addPropertiesStore(javaPropertiesStore)
                .addPropertiesStore(envPropertiesStore)
                .build();
    }
}

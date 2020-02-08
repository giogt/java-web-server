package org.giogt.web.server.config;

import org.giogt.web.server.properties.PropertySerializers;
import org.giogt.web.server.properties.stores.MapPropertiesStore;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigProviderTest {

    @Test
    public void getConfig_whenNoJavaPropertiesAndNoEnvVariablesAreSpecified_mustReturnDefaultValues()
            throws UnknownHostException {

        ConfigProvider configProvider = createInstance();
        WebServerConfig config = configProvider.getConfig();

        assertThat(config.getAddress(), is(InetAddress.getByName("0.0.0.0")));
        assertThat(config.getPort(), is(8080));
        assertThat(config.getNThreads(), is(50));
        assertThat(config.getIncomingConnectionsQueueSize(), is(50));
        assertThat(config.getRootDirPath(), is(Path.of("root/main")));
    }

    ConfigProvider createInstance() {
        return new ConfigProvider(
                new PropertySerializers(),
                MapPropertiesStore.builder().build());
    }
}

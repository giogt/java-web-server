package org.giogt.web.server.properties.stores;

import lombok.Data;
import org.giogt.web.server.properties.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilePropertiesStoreTest {

    private static final String TARGET_TEST_FILE = "build/test-file";

    @AfterEach
    public void afterEach() {
        new File(TARGET_TEST_FILE).delete();
    }

    @Test
    public void builder_withNullFileInInput_mustFail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> FilePropertiesStore.builder().build());
    }

    @Test
    public void builder_withNullDefaultPropertiesInput_mustCreateEmptyDefaultProperties() {
        FilePropertiesStore.Builder filePropertiesStoreBuilder = FilePropertiesStore.builder();
        filePropertiesStoreBuilder
                .withPropertiesFile(new File(TARGET_TEST_FILE))
                .build();

        assertThat(filePropertiesStoreBuilder.defaultProperties, is(new java.util.Properties()));
    }

    @Test
    public void builder_withEmptyConfigClassBean_mustCreateEmptyDefaultProperties() {
        FilePropertiesStore.Builder filePropertiesStoreBuilder = FilePropertiesStore.builder();
        filePropertiesStoreBuilder
                .withPropertiesFile(new File(TARGET_TEST_FILE))
                .withDefaultProperties(ComponentEmptyConfig.class)
                .build();

        assertThat(filePropertiesStoreBuilder.defaultProperties, is(new java.util.Properties()));
    }

    @Test
    public void builder_withDefaultProperties_mustParsePropertyAnnotatedBeanCorrectly() {
        FilePropertiesStore.Builder filePropertiesStoreBuilder = FilePropertiesStore.builder();
        filePropertiesStoreBuilder
                .withPropertiesFile(new File(TARGET_TEST_FILE))
                .withDefaultProperties(ComponentConfig.class);

        assertTestBeanProperties(filePropertiesStoreBuilder.defaultProperties);
    }


    @Test
    public void filePropertiesStore_whenPropertyFileDoesNotExist_mustCreateFileWithDefaultProperties()
            throws IOException {

        File propertiesFile = new File(TARGET_TEST_FILE);
        String charset = "UTF-8";

        FilePropertiesStore.builder()
                .withPropertiesFile(propertiesFile)
                .withCharset(charset)
                .withDefaultProperties(ComponentConfig.class)
                .build();

        assertTrue(propertiesFile.exists());
        java.util.Properties createdFileProperties = new java.util.Properties();

        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(propertiesFile), charset))) {
            createdFileProperties.load(reader);
            assertTestBeanProperties(createdFileProperties);
        }
    }

    @Test
    public void filePropertiesStore_whenPropertyFileDoesNotExist_mustLoadDefaultProperties() {

        File propertiesFile = new File(TARGET_TEST_FILE);
        String charset = "UTF-8";

        FilePropertiesStore filePropertiesStore = FilePropertiesStore.builder()
                .withPropertiesFile(propertiesFile)
                .withCharset(charset)
                .withDefaultProperties(ComponentConfig.class)
                .build();

        assertTestBeanPropertiesMap(filePropertiesStore.getProperties());
    }

    private void assertTestBeanPropertiesMap(Map<String, String> propertiesMap) {
        java.util.Properties properties = new java.util.Properties();
        for (Map.Entry<String, String> propertiesMapEntry : propertiesMap.entrySet()) {
            properties.setProperty(
                    propertiesMapEntry.getKey(), propertiesMapEntry.getValue());
        }
        assertTestBeanProperties(properties);
    }

    private void assertTestBeanProperties(java.util.Properties properties) {
        assertThat(properties, is(notNullValue()));
        assertThat(properties.isEmpty(), is(false));
        assertThat(properties.getProperty("component.enabled"), is(equalTo("false")));
        assertThat(properties.getProperty("component.host"), is(equalTo("localhost")));
        assertThat(properties.getProperty("component.port"), is(equalTo("12345")));
    }

    /**
     * Test Bean containing test configuration.
     */
    @Data
    private class ComponentConfig {
        @Property(key = "component.enabled", defaultValue = "false")
        private Boolean isComponentEnabled;

        @Property(key = "component.host", defaultValue = "localhost")
        private String componentHost;

        @Property(key = "component.port", defaultValue = "12345")
        private String componentPort;
    }

    /**
     * Test Bean containing test configuration.
     */
    private class ComponentEmptyConfig {
    }

}

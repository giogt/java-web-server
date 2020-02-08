package org.giogt.web.server.properties.stores;

import lombok.extern.slf4j.Slf4j;
import org.giogt.web.server.Preconditions;
import org.giogt.web.server.properties.PropertiesStore;
import org.giogt.web.server.properties.Property;
import org.giogt.web.server.properties.exceptions.CannotCreatePropertiesFileException;
import org.giogt.web.server.properties.exceptions.CannotLoadPropertiesFileException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class FilePropertiesStore implements PropertiesStore {

    private Properties properties = new Properties();

    private final File propertiesFile;
    private final Charset charset;
    private final Properties defaultProperties;

    private final Object refreshLock = new Object();

    FilePropertiesStore(
            File propertiesFile,
            Charset charset,
            Properties defaultProperties) {

        this.propertiesFile = propertiesFile;
        this.charset = charset;
        this.defaultProperties = defaultProperties;

        loadFromPropertiesFile();
    }

    @Override
    public String getProperty(String key) {
        synchronized (refreshLock) {
            return (String) properties.get(key);
        }
    }

    @Override
    public Map<String, String> getProperties() {
        synchronized (refreshLock) {

            Map<String, String> propertiesMap = new HashMap<>();
            for (Map.Entry<Object, Object> prop : properties.entrySet()) {
                propertiesMap.put((String) prop.getKey(), (String) prop.getValue());
            }
            return propertiesMap;

        }
    }

    @Override
    public void refresh() {
        loadFromPropertiesFile();
    }

    void loadFromPropertiesFile()
            throws CannotCreatePropertiesFileException, CannotLoadPropertiesFileException {

        synchronized (refreshLock) {

            // create file reader
            try (Reader fileReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(propertiesFile),
                            charset))) {
                properties = new Properties();
                properties.load(fileReader);
            } catch (FileNotFoundException e) {
                // file not found => create a new properties file with default properties
                createNewPropertiesFileWithDefaultProperties();
            } catch (IOException e) {
                throw new CannotLoadPropertiesFileException("cannot load specified properties file <" +
                        propertiesFile.getPath(),
                        e);
            }
        }
    }

    void createNewPropertiesFileWithDefaultProperties() {
        try (Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertiesFile), charset))) {
            log.warn("properties file <" + propertiesFile.getPath() + "> not found => attempting to create an empty file with default properties.");
            propertiesFile.createNewFile();
            log.info("properties file <" + propertiesFile.getPath() + "> successfully created");

            // create empty properties
            this.properties = this.defaultProperties;
            this.properties.store(fileWriter, propertiesFile.getName());

        } catch (IOException propertiesFileCreationException) {
            throw new CannotCreatePropertiesFileException(
                    "cannot create properties file <" + propertiesFile.getPath() + ">",
                    propertiesFileCreationException);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        File propertiesFile;
        Charset charset;
        Properties defaultProperties;

        public Builder withPropertiesFile(File propertiesFile) {
            this.propertiesFile = propertiesFile;
            return this;
        }

        public Builder withCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder withCharset(String charsetName) {
            this.charset = Charset.forName(charsetName);
            return this;
        }

        public Builder withDefaultProperties(Properties defaultProperties) {
            this.defaultProperties = defaultProperties;
            return this;
        }

        public Builder withDefaultProperties(Class<?> propertiesBean) {
            this.defaultProperties = new Properties();

            Class<?> currentClass = propertiesBean;
            while (currentClass != null) {
                processClassFields(currentClass, propertiesBean, defaultProperties);
                currentClass = currentClass.getSuperclass();
            }

            return this;
        }

        public FilePropertiesStore build()
                throws
                CannotLoadPropertiesFileException,
                CannotCreatePropertiesFileException {

            if (charset == null) {
                charset = DEFAULT_CHARSET;
            }
            if (defaultProperties == null) {
                defaultProperties = new Properties();
            }

            Preconditions.notNull(propertiesFile, "propertiesFile");
            return new FilePropertiesStore(propertiesFile, charset, defaultProperties);
        }

        private void processClassFields(
                Class<?> currentClass, Class<?> bean, Properties defaultProperties) {

            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                Property property = field.getAnnotation(Property.class);
                if (property != null) {
                    defaultProperties.setProperty(property.key(), property.defaultValue());
                }
            }
        }
    }
}

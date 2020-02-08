package org.giogt.web.server.properties.stores;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EnvPropertiesStoreTest {

    @Test
    public void buildKeyWithPrefix_whenPrefixIsNotPresent_mustReturnTheSameKey() {
        String key = "testKey";
        String expected = key;

        EnvPropertiesStore propertiesStore = createInstance(null, false);
        String result = propertiesStore.buildKeyWithPrefix(key);

        assertThat(result, is(expected));
    }

    @Test
    public void buildKeyWithPrefix_whenPrefixIsPresent_mustReturnTheKeyWithPrefix() {
        String prefix = "testPrefix.";
        String key = "testKey";
        String expected = prefix + key;

        EnvPropertiesStore propertiesStore = createInstance(prefix, false);
        String result = propertiesStore.buildKeyWithPrefix(key);

        assertThat(result, is(expected));
    }

    @Test
    public void convertKeyFormat_whenConvertKeysFormatIsFalse_mustReturnTheSameKey() {
        String key = "this.is-a_TesT";
        String expected = key;

        EnvPropertiesStore propertiesStore = createInstance(null, false);
        String result = propertiesStore.convertKeyFormat(key);

        assertThat(result, is(expected));
    }

    @Test
    public void convertKeyFormat_whenConvertKeysFormatIsTrue_mustReturnTheConvertedKey() {
        String key = "this.is-a_TesT";
        String expected = "THIS_IS_A_TEST";

        EnvPropertiesStore propertiesStore = createInstance(null, true);
        String result = propertiesStore.convertKeyFormat(key);

        assertThat(result, is(expected));
    }

    private EnvPropertiesStore createInstance(
            String prefix,
            boolean convertKeysFormat) {

        return new EnvPropertiesStore(prefix, convertKeysFormat);
    }
}
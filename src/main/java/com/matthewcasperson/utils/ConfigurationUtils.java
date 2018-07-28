package com.matthewcasperson.utils;

public interface ConfigurationUtils {
    /**
     * A generic config value reader
     * @param config The config name to read
     * @return The config value
     */
    String getConfigValue(final String config);
}

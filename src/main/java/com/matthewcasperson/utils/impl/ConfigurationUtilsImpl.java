package com.matthewcasperson.utils.impl;

import com.matthewcasperson.utils.ConfigurationUtils;

public class ConfigurationUtilsImpl implements ConfigurationUtils {
    @Override
    public String getConfigValue(String config) {
        if (System.getProperty(config) != null) return System.getProperty(config);
        return System.getenv(config);
    }
}

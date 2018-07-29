package com.matthewcasperson.datasources.impl;

import com.google.gson.JsonParser;
import com.matthewcasperson.datasources.Weather;
import com.matthewcasperson.utils.ConfigurationUtils;
import com.matthewcasperson.utils.WebUtils;
import com.matthewcasperson.utils.impl.ConfigurationUtilsImpl;
import com.matthewcasperson.utils.impl.WebUtilsImpl;

public class WeatherImpl implements Weather {
    private static final ConfigurationUtils CONFIGURATION_UTILS = new ConfigurationUtilsImpl();
    private static final WebUtils WEB_UTILS = new WebUtilsImpl();

    public boolean getRainToday() {
        try {
            final String weatherResponse = WEB_UTILS.HttpGet("https://api.darksky.net/forecast/" +
                    CONFIGURATION_UTILS.getConfigValue("WEATHER_API_KEY") +
                    "/27.2015,152.9655");
            final float rainForecast = new JsonParser().parse(weatherResponse)
                    .getAsJsonObject()
                    .getAsJsonObject("currently")
                    .get("precipProbability")
                    .getAsFloat();
            return rainForecast >= 0.5;
        } catch (final Exception ex) {
            return false;
        }
    }
}

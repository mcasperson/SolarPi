package com.matthewcasperson.datasources.impl;

import com.google.gson.JsonParser;
import com.matthewcasperson.datasources.Weather;
import com.matthewcasperson.utils.ConfigurationUtils;
import com.matthewcasperson.utils.Retry;
import com.matthewcasperson.utils.WebUtils;
import com.matthewcasperson.utils.impl.ConfigurationUtilsImpl;
import com.matthewcasperson.utils.impl.RetryImpl;
import com.matthewcasperson.utils.impl.WebUtilsImpl;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

public class WeatherImpl implements Weather {
    private static final Retry<Boolean, Exception> RETRY = new RetryImpl<>();
    private static final ConfigurationUtils CONFIGURATION_UTILS = new ConfigurationUtilsImpl();
    private static final WebUtils WEB_UTILS = new WebUtilsImpl();

    public boolean getRainToday() {
        try {
            return RETRY.retry(new RetryCallback<Boolean, Exception>() {
                @Override
                public Boolean doWithRetry(RetryContext context) throws Exception {
                    final String weatherResponse = WEB_UTILS.HttpGet("https://api.darksky.net/forecast/" +
                            CONFIGURATION_UTILS.getConfigValue("WEATHER_API_KEY") +
                            "/27.2015,152.9655");
                    final float rainForecast = new JsonParser().parse(weatherResponse)
                            .getAsJsonObject()
                            .getAsJsonArray("daily")
                            .get(0)
                            .getAsJsonObject()
                            .get("precipProbability")
                            .getAsFloat();
                    return rainForecast >= 0.5;
                }
            });
        } catch (final Throwable ex) {
            System.err.println(ex);
            return false;
        }
    }
}

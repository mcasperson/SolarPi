package com.matthewcasperson.displays.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.Pixel;
import com.matthewcasperson.displays.Display;
import com.matthewcasperson.effects.impl.RainEffect;
import com.matthewcasperson.utils.ConfigurationUtils;
import com.matthewcasperson.utils.impl.ConfigurationUtilsImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.matthewcasperson.Constants.MAX_BRIGHTNESS;

public class SolarDisplay implements Display {

    private static final ConfigurationUtils CONFIGURATION_UTILS = new ConfigurationUtilsImpl();
    public static final int TIMEOUT = 5;
    private static final int MAX_USAGE = 2500;
    private static final int MAX_FAILURES = 3;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    private static final String CURRENT_OUTPUT_ELEMENT = "tr.tr1:nth-child(5) > td:nth-child(2)";
    private static final String SOLAR_USER = "SOLAR_USER";
    private static final String SOLAR_PASS = "SOLAR_PASS";
    private static final String SOLAR_URL = "SOLAR_URL";
    private static final RainEffect RAIN_EFFECT = new RainEffect(MAX_BRIGHTNESS, 4.0f);
    private int failureCount;
    private boolean raining = true;
    private Pixel lastResult = new Pixel();

    private void setRaining(final boolean raining, final Blinkt blinkt) {
        if (raining) {
            if (!this.raining) {
                for (int i = 2; i < 8; ++i) {
                    blinkt.setPixel(i, (float)Math.random() * MAX_BRIGHTNESS);
                }
            }
        }

        this.raining = raining;
    }

    @Override
    public void calculate(final Blinkt blinkt) {
        setStatus(blinkt, getWatts());
    }

    @Override
    public void update(final Blinkt blink, final float delta) {
        if (raining) {
            for (int i = 2; i < 8; ++i) {
                RAIN_EFFECT.update(i, blink, delta);
            }
        }
    }

    /**
     * Turns on the leds based on the solar output.
     * Green = 100% or over
     * Yellow = 50% to 100%
     * Red = 0% to 50%
     * @param watts The current solar output
     */
    private void setStatus(final Blinkt blinkt, final float watts) {
        System.out.print(DATE_FORMAT.format(new Date()) + " Setting status to " + watts);

        if (watts >= MAX_USAGE) {
            System.out.println(" (GREEN)");
            lastResult = new Pixel(0, 255, 0);
        } else if (watts >= MAX_USAGE / 2 ) {
            System.out.println(" (YELLOW)");
            lastResult = new Pixel(255, 255, 0);
        } else if (watts >= 0) {
            System.out.println(" (RED)");
            lastResult = new Pixel(255, 0, 0);
        } else {
            System.out.println(" (ERROR)");
        }

        blinkt.setAll(lastResult.r, lastResult.g, lastResult.b);
    }


    /**
     * Query the solar web page, read the output HTML, and parse it.
     * @return The solar output in watts
     */
    private float getWatts() {
        final String status = getSolarWebPage();
        final String output = parseHtmlResult(status);

        final float value = NumberUtils.toFloat(output.replaceAll("[^0-9.]*", ""), -1);
        if  (output.endsWith("kW")) {
            return value * 1000;
        }

        if  (output.endsWith("W")) {
            return value;
        }

        return -1;
    }

    /**
     * Log into the solar web page and return the raw HTML
     * @return The raw solar status page HTML
     */
    private String getSolarWebPage()  {
        try {
            final CredentialsProvider provider = new BasicCredentialsProvider();
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    CONFIGURATION_UTILS.getConfigValue(SOLAR_USER),
                    CONFIGURATION_UTILS.getConfigValue(SOLAR_PASS));
            provider.setCredentials(AuthScope.ANY, credentials);

            final RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(TIMEOUT * 1000)
                    .setConnectionRequestTimeout(TIMEOUT * 1000)
                    .setSocketTimeout(TIMEOUT * 1000).build();

            try (final CloseableHttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                    .setDefaultRequestConfig(config)
                    .build()) {
                try (final CloseableHttpResponse response = client.execute(new HttpGet(CONFIGURATION_UTILS.getConfigValue(SOLAR_URL)))) {
                    final HttpEntity entity = response.getEntity();
                    try {
                        // all good, so reset the failure count
                        failureCount = 0;
                        return IOUtils.toString(entity.getContent(), Charset.forName("UTF-8"));
                    } finally {
                        EntityUtils.consumeQuietly(entity);
                    }
                }
            }

        } catch (final Exception ex) {
            /*
                Successive failures will exit the application, and use supervisord to restart it
             */
            ++failureCount;
            if (failureCount >= MAX_FAILURES) {

                System.err.println(DATE_FORMAT.format(new Date()) + " Failure count exceeded, exiting");
                System.exit(1);
            }

            /*
                Any issues with the HTTP request are logged, and we return a blank string.
                This will result in the blinking red light calculate.
             */
            System.err.println(DATE_FORMAT.format(new Date()) + " " + ex.toString());
            return "";
        }
    }

    /**
     * Extract the solar output from the raw HTML
     * @param value The raw HTML
     * @return The solar output
     */
    private String parseHtmlResult(final String value) {
        final Document doc = Jsoup.parse(value);
        return doc.select(CURRENT_OUTPUT_ELEMENT).text().trim();
    }
}

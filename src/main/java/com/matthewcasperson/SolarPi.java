package com.matthewcasperson;

import com.pi4j.io.gpio.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SolarPi {
    /**
     * Anything higher than this value will result in a green display
     */
    private static final int MAX_USAGE = 2500;
    private static final int MAX_FAILURES = 3;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    private static final String CURRENT_OUTPUT_ELEMENT = "tr.tr1:nth-child(5) > td:nth-child(2)";
    private static final String SOLAR_USER = "SOLAR_USER";
    private static final String SOLAR_PASS = "SOLAR_PASS";
    private static final String SOLAR_URL = "SOLAR_URL";
    private static final int REFRESH_PERIOD = 20000;
    private static final int INITIAL_TEST_PERIOD = 1000;
    private GpioController gpio;
    private GpioPinDigitalOutput red;
    private GpioPinDigitalOutput yellow;
    private GpioPinDigitalOutput green;

    private int failureCount = 0;

    public static void main(final String[] args) {
        new SolarPi();
    }

    public SolarPi() {
        init();
        initialLedTest();

        while (true) {
            setStatus(getWatts());
            sleep(REFRESH_PERIOD);
        }
    }

    /**
     * Build the pin objects.
     */
    private void init() {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        gpio = GpioFactory.getInstance();
        red = getPin(RaspiBcmPin.GPIO_13, "Red");
        yellow = getPin(RaspiBcmPin.GPIO_19, "Yellow");
        green = getPin(RaspiBcmPin.GPIO_26, "Green");
    }

    /**
     * A boot up sequence that cycles through the leds.
     */
    private void initialLedTest() {
        red.pulse(INITIAL_TEST_PERIOD, true);
        yellow.pulse(INITIAL_TEST_PERIOD, true);
        green.pulse(INITIAL_TEST_PERIOD, true);
    }

    /**
     * Turns on the leds based on the solar output.
     * Green = 100% or over
     * Yellow = 50% to 100%
     * Red = 0% to 50%
     * @param watts The current solar output
     */
    private void setStatus(final float watts) {
        System.out.println(DATE_FORMAT.format(new Date()) + " Setting status to " + watts);

        green.low();
        yellow.low();
        red.low();

        if (watts >= MAX_USAGE) {
            green.high();
        } else if (watts >= MAX_USAGE / 2 ) {
            yellow.high();
        } else if (watts >= 0) {
            red.high();
        } else {
            red.blink(200);
        }
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
     * Build a pin object
     * @param pin The pin number
     * @param name The pin name
     * @return The configured pin object
     */
    private GpioPinDigitalOutput getPin(final Pin pin, final String name) {
        final GpioPinDigitalOutput gpioPin = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW);
        gpioPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        return gpioPin;
    }

    /**
     * Log into the solar web page and return the raw HTML
     * @return The raw solar status page HTML
     */
    private String getSolarWebPage()  {
        try {
            final CredentialsProvider provider = new BasicCredentialsProvider();
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(getConfigValue(SOLAR_USER), getConfigValue(SOLAR_PASS));
            provider.setCredentials(AuthScope.ANY, credentials);

            try (final CloseableHttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build()) {
                try (final CloseableHttpResponse response = client.execute(new HttpGet(getConfigValue(SOLAR_URL)))) {
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
                This will result in the blinking red light display.
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

    /**
     * A no-throw sleep method
     * @param milliseconds How long to sleep for
     */
    private void sleep(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            // ignored
        }
    }

    /**
     * A generic config value reader
     * @param config The config name to read
     * @return The config value
     */
    private String getConfigValue(final String config) {
        if (System.getProperty(config) != null) return System.getProperty(config);
        return System.getenv(config);
    }
}



package com.matthewcasperson;

import com.pi4j.io.gpio.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SolarPi {
    /**
     * Anything higher than this value will result in a green display
     */
    private final int MAX_USAGE = 2500;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm dd-MM-yyyy");
    private static final String CURRENT_OUTPUT_ELEMENT = "tr.tr1:nth-child(5) > td:nth-child(2)";
    private static final String SOLAR_USER = "SOLAR_USER";
    private static final String SOLAR_PASS = "SOLAR_PASS";
    private static final String SOLAR_URL = "SOLAR_URL";
    private static final int REFRESH_PERIOD = 10000;
    private static final int INITIAL_TEST_PERIOD = 1000;
    private GpioController gpio;
    private GpioPinDigitalOutput red;
    private GpioPinDigitalOutput yellow;
    private GpioPinDigitalOutput green;

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

    private void init() {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        gpio = GpioFactory.getInstance();
        red = getPin(RaspiBcmPin.GPIO_13, "Red");
        yellow = getPin(RaspiBcmPin.GPIO_19, "Yellow");
        green = getPin(RaspiBcmPin.GPIO_26, "Green");
    }

    private void initialLedTest() {
        red.pulse(INITIAL_TEST_PERIOD, true);
        yellow.pulse(INITIAL_TEST_PERIOD, true);
        green.pulse(INITIAL_TEST_PERIOD, true);
    }

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

    private GpioPinDigitalOutput getPin(final Pin pin, final String name) {
        final GpioPinDigitalOutput gpioPin = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW);
        gpioPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        return gpioPin;
    }

    private String getSolarWebPage()  {
        try {
            final CredentialsProvider provider = new BasicCredentialsProvider();
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(getConfigValue(SOLAR_USER), getConfigValue(SOLAR_PASS));
            provider.setCredentials(AuthScope.ANY, credentials);

            final HttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();

            final HttpResponse response = client.execute(new HttpGet(getConfigValue(SOLAR_URL)));
            return IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));

        } catch (final Exception ex) {
            return "";
        }
    }

    private String parseHtmlResult(final String value) {
        final Document doc = Jsoup.parse(value);
        return doc.select(CURRENT_OUTPUT_ELEMENT).text();
    }

    private void sleep(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            // ignored
        }
    }

    private String getConfigValue(final String config) {
        if (System.getProperty(config) != null) return System.getProperty(config);
        return System.getenv(config);
    }
}



package com.matthewcasperson;

import com.pi4j.io.gpio.*;
import org.apache.commons.io.IOUtils;
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

public class SolarPi {
    private final GpioController gpio;
    private final GpioPinDigitalOutput red;
    private final GpioPinDigitalOutput yellow;
    private final GpioPinDigitalOutput green;
    private final int MAX_USAGE = 2500;

    public static void main(final String[] args) {
        final SolarPi solarPi = new SolarPi();
    }

    public SolarPi() {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        gpio = GpioFactory.getInstance();
        red = getPin(RaspiBcmPin.GPIO_26, "Red");
        yellow = getPin(RaspiBcmPin.GPIO_19, "Yellow");
        green = getPin(RaspiBcmPin.GPIO_13, "Green");

        initialLedTest();

        while (true) {
            setStatus(getWatts());
            sleep(10000);
        }
    }

    private void initialLedTest() {
        red.pulse(1000, true);
        yellow.pulse(1000, true);
        green.pulse(1000, true);
    }

    private void setStatus(int watts) {
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

    private int getWatts() {
        final String status = getSolarStatus();
        if  (status.endsWith("kW")) {
            return Integer.parseInt(status.replaceAll("[^0-9]*", "")) * 1000;
        }

        if  (status.endsWith("W")) {
            return Integer.parseInt(status.replaceAll("[^0-9]*", ""));
        }

        return -1;
    }

    private GpioPinDigitalOutput getPin(final Pin pin, final String name) {
        final GpioPinDigitalOutput gpioPin = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW);
        gpioPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        return gpioPin;
    }

    private String getSolarStatus()  {
        try {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(System.getenv("SOLAR_USER"), System.getenv("SOLAR_PASS"));
            provider.setCredentials(AuthScope.ANY, credentials);

            HttpClient client = HttpClientBuilder.create()
                    .setDefaultCredentialsProvider(provider)
                    .build();

            HttpResponse response = client.execute(
                    new HttpGet(System.getenv("SOLAR_URL")));

            String value = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));

            Document doc = Jsoup.parse(value);
            return doc.select("tr.tr1:nth-child(5) > td:nth-child(2)").text();
        } catch (final IOException ex) {
            return "";
        }
    }

    private void sleep(final int milliseconds) {
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            // ignored
        }
    }
}



package com.matthewcasperson;

import com.pi4j.io.gpio.*;

public class SolarPi {
    private final GpioController gpio;
    private final GpioPinDigitalOutput red;
    private final GpioPinDigitalOutput yellow;
    private final GpioPinDigitalOutput green;

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

        System.exit(0);
    }

    private void initialLedTest() {
        red.pulse(1000, true);
        yellow.pulse(1000, true);
        green.pulse(1000, true);
    }

    private GpioPinDigitalOutput getPin(final Pin pin, final String name) {
        final GpioPinDigitalOutput gpioPin = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW);
        gpioPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        return gpioPin;
    }

    private void sleep(final int milliseconds) {
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            // ignored
        }
    }
}



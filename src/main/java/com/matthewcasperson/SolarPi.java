package com.matthewcasperson;

import com.pi4j.io.gpio.*;

public class SolarPi {
    public static void main(String[] args) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput red = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_26, "Red", PinState.HIGH);
        final GpioPinDigitalOutput yellow = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_19, "Yellow", PinState.HIGH);
        final GpioPinDigitalOutput green = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_13, "Green", PinState.HIGH);
    }
}



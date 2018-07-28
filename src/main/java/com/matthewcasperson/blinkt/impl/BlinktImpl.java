package com.matthewcasperson.blinkt.impl;

import com.matthewcasperson.utils.GeneralUtils;
import com.matthewcasperson.utils.impl.GeneralUtilsImpl;
import com.pi4j.io.gpio.*;

public class BlinktImpl extends NoOpBlinktImpl {
    static {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
    }

    private static final GeneralUtils GENERAL_UTILS = new GeneralUtilsImpl();
    private static final GpioController gpio = GpioFactory.getInstance();

    private final GpioPinDigitalOutput dat;
    private final GpioPinDigitalOutput clk;
    private boolean clearOnExit = true;

    @Override
    public void setClearOnExit(final boolean clearOnExit) {
        this.clearOnExit = clearOnExit;
    }

    public BlinktImpl() {
        dat = getDigitalPin(RaspiBcmPin.GPIO_23, "DAT");
        clk = getDigitalPin(RaspiBcmPin.GPIO_24, "CLK");
    }

    @Override
    public void exit() {
        if (clearOnExit) {
            clear();
            show();
        }
    }



    private void writeByte(int input) {
        for (int x = 0; x < 8; ++x) {
            if ((input & 0b10000000) == 0) {
                dat.low();
            } else {
                dat.high();
            }
            clk.high();
            GENERAL_UTILS.sleep(0, 500);
            input <<=1;
            clk.low();
            GENERAL_UTILS.sleep(0, 500);
        }
    }

    private void eof() {
        dat.low();
        for (int x = 0; x < 36; ++x) {
            clk.high();
            GENERAL_UTILS.sleep(0, 500);
            clk.low();
            GENERAL_UTILS.sleep(0, 500);
        }
    }

    private void sof() {
        dat.low();
        for (int x = 0; x < 32; ++x) {
            clk.high();
            GENERAL_UTILS.sleep(0, 500);
            clk.low();
            GENERAL_UTILS.sleep(0, 500);
        }
    }

    @Override
    public void show() {
        sof();

        for (int[] pixel : pixels) {
            int r = pixel[0];
            int g = pixel[1];
            int b = pixel[2];
            int brightness = pixel[3];

            writeByte(0b11100000 | brightness);
            writeByte(b);
            writeByte(g);
            writeByte(r);
        }

        eof();
    }

    /**
     * Build a pin object
     * @param pin The pin number
     * @param name The pin name
     * @return The configured pin object
     */
    private GpioPinDigitalOutput getDigitalPin(final Pin pin, final String name) {
        final GpioPinDigitalOutput gpioPin = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW);
        gpioPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        return gpioPin;
    }
}

package com.matthewcasperson;

import com.pi4j.io.gpio.*;

public class Blinkt {
    private static final int NUM_PIXELS = 8;
    private static final int BRIGHTNESS = 7;

    private final GpioController gpio = GpioFactory.getInstance();

    private int[][] pixels = new int[][] {
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS}
    };
    private boolean clearOnExit = true;
    private GpioPinPwmOutput dat;
    private GpioPinPwmOutput clk;

    public void setClearOnExit(final boolean clearOnExit) {
        this.clearOnExit = clearOnExit;
    }

    public Blinkt() {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        dat = getPin(RaspiBcmPin.GPIO_23, "DAT");
        clk = getPin(RaspiBcmPin.GPIO_24, "CLK");
    }

    public void exit() {
        if (clearOnExit) {
            clear();
            show();
        }
    }

    /**
     * Set the brightness of all pixels
     */
    public void setBrightness(float brightness) {
        if (brightness < 0 || brightness > 1){
            throw new RuntimeException ("Brightness should be between 0.0 and 1.0");
        }

        for (int x = 0; x < NUM_PIXELS; ++x) {
            pixels[x][3] = (int)(31.0 * brightness) & 0b11111;
        }
    }

    public void clear() {
        for (int x = 0; x < NUM_PIXELS; ++x) {
            pixels[x] = new int[] {0, 0, 0};
        }
    }

    public void writeByte(int input) {
        for (int x =0; x < 8; ++x) {
            dat.setPwm(input & 0b10000000);
            clk.setPwm(1);
            sleep(0, 500);
            input <<=1;
            clk.setPwm(0);
            sleep(0, 500);
        }
    }

    public void eof() {
        dat.setPwm(0);
        for (int x = 0; x < 36; ++x) {
            clk.setPwm(1);
            sleep(0, 500);
            clk.setPwm(0);
            sleep(0, 500);
        }
    }

    public void sof() {
        dat.setPwm(0);
        for (int x = 0; x < 32; ++x) {
            clk.setPwm(1);
            sleep(0, 500);
            clk.setPwm(0);
            sleep(0, 500);
        }
    }

    public void show() {
        sof();

        for (int[] pixel : pixels) {
            int r = pixel[0];
            int g = pixel[1];
            int b = pixel[2];
            int brightness = pixel[3];

            writeByte((byte)(0b11100000 | brightness));
            writeByte(b);
            writeByte(g);
            writeByte(r);
        }

        eof();
    }

    /**
     * Set the RGB value and optionally brightness of all pixels.
     * If you don 't supply a brightness value, the last value set for each pixel be kept.
     * @param r Amount of red: 0 to 255
     * @param g Amount of green: 0 to 255
     * @param b Amount of blue: 0 to 255
     * @param brightness Brightness: 0.0 to 1.0
     */
    public void setAll(int r, int g, int b, float brightness) {
        for (int x =0 ; x < NUM_PIXELS; ++x) {
            setPixel(x, r, g, b, brightness);
        }
    }

    /**
     * Get the RGB and brightness value of a specific pixel
     */
    public Pixel getPixel(int x) {

        int r = pixels[x][0];
        int g = pixels[x][1];
        int b = pixels[x][2];
        float brightness = (float)pixels[x][3];
        brightness /= 31.0;

        return new Pixel(r, g, b, Math.round(brightness));
    }

    /**
     * Set the RGB value, and optionally brightness, of a single pixel
     * If you don't supply a brightness value, the last value will be kept.
     */
    public void setPixel(int x, int r, int g, int b, float brightness) {
        if (brightness == 0) {
            brightness = pixels[x][3];
        } else {
            brightness = (int)(31.0 * brightness) & 0b11111;
        }

        pixels[x] = new int[] {
                r & 0xff,
                g & 0xff,
                b & 0xff,
                brightness == 0
                        ? pixels[x][3]
                        : (int)(31.0 * brightness) & 0b11111
        };
    }

    /**
     * Set the RGB value, and optionally brightness, of a single pixel
     * If you don't supply a brightness value, the last value will be kept.
     */
    public void setPixel(int x, int r, int g, int b) {
        pixels[x] = new int[] {
                r & 0xff,
                g & 0xff,
                b & 0xff,
                pixels[x][3]
        };
    }

    /**
     * A no-throw sleep method
     * @param milliseconds How long to sleep for
     */
    private void sleep(final int milliseconds, final int nanoseconds) {
        try {
            Thread.sleep(milliseconds, nanoseconds);
        } catch (final InterruptedException e) {
            // ignored
        }
    }

    /**
     * Build a pin object
     * @param pin The pin number
     * @param name The pin name
     * @return The configured pin object
     */
    private GpioPinPwmOutput getPin(final Pin pin, final String name) {
        final GpioPinPwmOutput gpioPin = gpio.provisionPwmOutputPin(pin, name, 0);
        gpioPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        return gpioPin;
    }
}

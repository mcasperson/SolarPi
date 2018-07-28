package com.matthewcasperson.blinkt.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.Pixel;
import org.apache.commons.math3.util.Precision;

public class NoOpBlinkImpl implements Blinkt {
    private static final int NUM_PIXELS = 8;
    private static final int BRIGHTNESS = 7;

    protected final int[][] pixels = new int[][] {
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS},
            {0, 0, 0, BRIGHTNESS}
    };

    @Override
    public void setClearOnExit(boolean clearOnExit) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void setBrightness(float brightness) {
        if (brightness < 0 || brightness > 1){
            throw new RuntimeException ("Brightness should be between 0.0 and 1.0");
        }

        for (int x = 0; x < NUM_PIXELS; ++x) {
            pixels[x][3] = (int)(31.0 * brightness) & 0b11111;
        }
    }

    @Override
    public void clear() {
        for (int x = 0; x < NUM_PIXELS; ++x) {
            pixels[x][0] = 0;
            pixels[x][1] = 0;
            pixels[x][2] = 0;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void setAll(int r, int g, int b, float brightness) {
        for (int x =0 ; x < NUM_PIXELS; ++x) {
            setPixel(x, r, g, b, brightness);
        }
    }

    @Override
    public void setAll(int r, int g, int b) {
        for (int x =0 ; x < NUM_PIXELS; ++x) {
            setPixel(x, r, g, b);
        }
    }

    @Override
    public Pixel getPixel(int x) {

        int r = pixels[x][0];
        int g = pixels[x][1];
        int b = pixels[x][2];
        float brightness = (float)pixels[x][3];
        brightness /= 31.0;

        return new Pixel(r, g, b, Precision.round(brightness, 3));
    }

    @Override
    public void setPixel(int x, int r, int g, int b, float brightness) {
        pixels[x][0] = r & 0xff;
        pixels[x][1] = g & 0xff;
        pixels[x][2] = b & 0xff;
        pixels[x][3] = brightness == 0
                ? pixels[x][3]
                : (int)(31.0 * brightness) & 0b11111;
    }

    @Override
    public void setPixel(int x, int r, int g, int b) {
        pixels[x][0] = r & 0xff;
        pixels[x][1] = g & 0xff;
        pixels[x][2] = b & 0xff;
    }

    @Override
    public void setPixel(int x, float brightness) {
        pixels[x][3] = brightness == 0
                ? pixels[x][3]
                : (int)(31.0 * brightness) & 0b11111;
    }
}

package com.matthewcasperson.blinkt;

public class Pixel {
    public int r;
    public int g;
    public int b;
    public float brightness;

    public Pixel() {

    }

    public Pixel(int r, int g, int b, float brightness) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.brightness = brightness;
    }
}

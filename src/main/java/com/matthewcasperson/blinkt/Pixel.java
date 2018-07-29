package com.matthewcasperson.blinkt;

public class Pixel {
    private int r;
    private int g;
    private int b;
    private float brightness;

    public Pixel() {

    }

    public Pixel(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Pixel(int r, int g, int b, float brightness) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.brightness = brightness;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public float getBrightness() {
        return brightness;
    }
}

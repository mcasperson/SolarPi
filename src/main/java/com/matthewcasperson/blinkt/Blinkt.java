package com.matthewcasperson.blinkt;

public interface Blinkt {
    void setClearOnExit(boolean clearOnExit);

    void exit();

    /**
     * Set the brightness of all pixels
     */
    void setBrightness(float brightness);

    void clear();

    void show();

    /**
     * Set the RGB value and optionally brightness of all pixels.
     * If you don 't supply a brightness value, the last value set for each pixel be kept.
     * @param r Amount of red: 0 to 255
     * @param g Amount of green: 0 to 255
     * @param b Amount of blue: 0 to 255
     * @param brightness Brightness: 0.0 to 1.0
     */
    void setAll(int r, int g, int b, float brightness);

    /**
     * Set the RGB value and optionally brightness of all pixels.
     * If you don 't supply a brightness value, the last value set for each pixel be kept.
     * @param r Amount of red: 0 to 255
     * @param g Amount of green: 0 to 255
     * @param b Amount of blue: 0 to 255
     */
    void setAll(int r, int g, int b);

    /**
     * Get the RGB and brightness value of a specific pixel
     */
    Pixel getPixel(int x);

    /**
     * Set the RGB value, and optionally brightness, of a single pixel
     * If you don't supply a brightness value, the last value will be kept.
     */
    void setPixel(int x, int r, int g, int b, float brightness);

    /**
     * Set the RGB value, and optionally brightness, of a single pixel
     * If you don't supply a brightness value, the last value will be kept.
     */
    void setPixel(int x, int r, int g, int b);

    /**
     * Set the RGB value, and optionally brightness, of a single pixel
     * If you don't supply a brightness value, the last value will be kept.
     */
    void setPixel(int x, float brightness);
}

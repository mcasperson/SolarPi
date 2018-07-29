package com.matthewcasperson.effects.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.Pixel;
import com.matthewcasperson.effects.Effect;

/**
 * An effect to represent rain. Pixels cycle from dark to max brightness and back to dark.
 */
public class RainEffect implements Effect {
    final float maxBrightness;
    final float cyclePeriod;

    public RainEffect(final float maxBrightness, final float cyclePeriod) {
        this.maxBrightness = maxBrightness;
        this.cyclePeriod = cyclePeriod;
    }

    public void update(final int pixel, final Blinkt blinkt, final float delta) {
        if (delta != 0) {
            final Pixel blinktPixel = blinkt.getPixel(pixel);
            final float change = delta / cyclePeriod * maxBrightness;
            final float brightness = blinktPixel.brightness + change;
            final float newBrightness = brightness > maxBrightness ? 0 : brightness;

            blinkt.setPixel(pixel, newBrightness);
        }
    }
}

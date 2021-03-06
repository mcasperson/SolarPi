package com.matthewcasperson.effects.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.Pixel;
import com.matthewcasperson.effects.Effect;

import static com.matthewcasperson.Constants.MIN_BRIGHTNESS_CHANGE;

/**
 * An effect to represent rain. Pixels cycle from dark to max brightness and back to dark.
 */
public class SparkleEffect implements Effect {

    final float maxBrightness;
    final float cyclePeriod;

    public SparkleEffect(final float maxBrightness, final float cyclePeriod) {
        this.maxBrightness = maxBrightness;
        this.cyclePeriod = cyclePeriod;
    }

    public void update(final int pixel, final Blinkt blinkt, final float delta) {
        if (delta != 0) {
            final Pixel blinktPixel = blinkt.getPixel(pixel);
            final float change = delta / cyclePeriod * maxBrightness;
            final float brightness = blinktPixel.getBrightness() + Math.max(change, MIN_BRIGHTNESS_CHANGE);
            final float newBrightness = brightness > maxBrightness ? MIN_BRIGHTNESS_CHANGE : brightness;

            blinkt.setPixel(pixel, newBrightness);
        }
    }
}

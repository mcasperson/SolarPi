package com.matthewcasperson.effects.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.Pixel;
import com.matthewcasperson.effects.Effect;

public class RainEffect implements Effect {
    final float maxBrightness;
    final float cyclePeriod;

    public RainEffect(final float maxBrightness, final float cyclePeriod) {
        this.maxBrightness = maxBrightness;
        this.cyclePeriod = cyclePeriod;
    }

    public void update(final int pixel, final Blinkt blinkt, final float delta) {
        final float change = delta / cyclePeriod * maxBrightness;
        final Pixel blinktPixel = blinkt.getPixel(pixel);
        final float brightness = blinktPixel.brightness + change;

        blinkt.setPixel(
                pixel,
                blinktPixel.r,
                blinktPixel.g,
                blinktPixel.b,
                brightness > maxBrightness ? 0 : brightness);
    }
}

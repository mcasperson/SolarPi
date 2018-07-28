package com.matthewcasperson.effects;

import com.matthewcasperson.blinkt.Blinkt;

/**
 * Effects are animations that are applied between data calculations.
 */
public interface Effect {
    void update(final int pixel, final Blinkt blinkt, final float delta);
}

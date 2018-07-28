package com.matthewcasperson.effects;

import com.matthewcasperson.blinkt.Blinkt;

public interface Effect {
    void update(final int pixel, final Blinkt blinkt, final float delta);
}

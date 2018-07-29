package com.matthewcasperson.effects.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.effects.Effect;

public class BlinkEffect implements Effect {
    private boolean state;

    @Override
    public void update(int pixel, Blinkt blinkt, float delta) {
        state = !state;
        if (state) {
            blinkt.setPixel(pixel, 255, 0, 255);
        } else {
            blinkt.setPixel(pixel, 0, 0, 0);
        }
    }
}

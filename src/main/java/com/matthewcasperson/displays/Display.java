package com.matthewcasperson.displays;

import com.matthewcasperson.blinkt.Blinkt;

public interface Display {
    void init(final Blinkt blinkt);
    void calculateMinute(final Blinkt blinkt);
    void calculateDay(final Blinkt blinkt);
    void update(final Blinkt blink, final float delta);
}

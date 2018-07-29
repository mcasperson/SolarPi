package com.matthewcasperson.displays.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.datasources.Fuel;
import com.matthewcasperson.datasources.impl.AlwaysGoodFuelImpl;
import com.matthewcasperson.datasources.impl.FuelImpl;
import com.matthewcasperson.displays.Display;
import com.matthewcasperson.effects.impl.BlinkEffect;

public class FuelDisplay implements Display {
    private static final Fuel FUEL = new AlwaysGoodFuelImpl();
    private static final BlinkEffect BLINK_EFFECT = new BlinkEffect();
    private boolean isGoodFuelDay = false;

    @Override
    public void init(Blinkt blinkt) {

    }

    @Override
    public void calculateMinute(final Blinkt blinkt) {

    }

    @Override
    public void calculateDay(final Blinkt blinkt) {
        try {
            this.isGoodFuelDay = FUEL.isGoodFuelDay();
        } catch (final Exception ex) {
            this.isGoodFuelDay = false;
        }
    }

    @Override
    public void update(final Blinkt blink, float delta) {
        if (isGoodFuelDay) {
            BLINK_EFFECT.update(0, blink, delta);
        }
    }
}

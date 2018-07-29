package com.matthewcasperson.displays.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.Pixel;
import com.matthewcasperson.datasources.SolarSystem;
import com.matthewcasperson.datasources.impl.SolarSystemImpl;
import com.matthewcasperson.displays.Display;

import java.util.Date;

import static com.matthewcasperson.Constants.DATE_FORMAT;

public class SolarDisplay implements Display {

    private static final int MAX_USAGE = 2500;

    private final SolarSystem solarSystem = new SolarSystemImpl();

    private Pixel lastResult = new Pixel();

    public void init(final Blinkt blinkt) {

    }

    @Override
    public void calculateMinute(final Blinkt blinkt) {
        setStatus(blinkt, solarSystem.getWatts());
    }

    @Override
    public void calculateDay(Blinkt blinkt) {

    }

    @Override
    public void update(final Blinkt blink, final float delta) {

    }

    /**
     * Turns on the leds based on the solar output.
     * Green = 100% or over
     * Yellow = 50% to 100%
     * Red = 0% to 50%
     * @param watts The current solar output
     */
    private void setStatus(final Blinkt blinkt, final float watts) {
        System.out.print(DATE_FORMAT.format(new Date()) + " Setting status to " + watts);

        if (watts >= MAX_USAGE) {
            System.out.println(" (GREEN)");
            lastResult = new Pixel(0, 255, 0);
        } else if (watts >= MAX_USAGE / 2 ) {
            System.out.println(" (YELLOW)");
            lastResult = new Pixel(255, 255, 0);
        } else if (watts >= 0) {
            System.out.println(" (RED)");
            lastResult = new Pixel(255, 0, 0);
        } else {
            System.out.println(" (ERROR)");
        }

        blinkt.setAll(lastResult.getR(), lastResult.getG(), lastResult.getB());
    }
}

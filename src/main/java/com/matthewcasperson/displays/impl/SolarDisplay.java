package com.matthewcasperson.displays.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.Pixel;
import com.matthewcasperson.datasources.SolarSystem;
import com.matthewcasperson.datasources.impl.SolarSystemImpl;
import com.matthewcasperson.datasources.Weather;
import com.matthewcasperson.datasources.impl.AlwaysRainWeather;
import com.matthewcasperson.datasources.impl.WeatherImpl;
import com.matthewcasperson.displays.Display;
import com.matthewcasperson.effects.impl.SparkleEffect;

import java.util.Date;

import static com.matthewcasperson.Constants.DATE_FORMAT;
import static com.matthewcasperson.Constants.MAX_BRIGHTNESS;

public class SolarDisplay implements Display {

    private static final int MAX_USAGE = 2500;
    private static final float RAIN_CYCLE_EFFECT_PERIOD = 6.0f;
    private static final SparkleEffect RAIN_EFFECT = new SparkleEffect(MAX_BRIGHTNESS, RAIN_CYCLE_EFFECT_PERIOD);

    private final SolarSystem solarSystem = new SolarSystemImpl();
    private final Weather weather = new WeatherImpl();
    private boolean raining = false;
    private Pixel lastResult = new Pixel();

    private void setRaining(final boolean raining, final Blinkt blinkt) {
        if (raining) {
            if (!this.raining) {
                for (int i = 0; i < 6; ++i) {
                    blinkt.setPixel(i, (float)Math.random() * MAX_BRIGHTNESS);
                }
            }
        } else {
            for (int i = 0; i < 6; ++i) {
                blinkt.setPixel(i, MAX_BRIGHTNESS);
            }
        }

        this.raining = raining;
        System.out.println("Rain forecast today: " + this.raining);
    }

    public void init(final Blinkt blinkt) {
        setRaining(true, blinkt);
    }

    @Override
    public void calculateMinute(final Blinkt blinkt) {
        setStatus(blinkt, solarSystem.getWatts());
    }

    @Override
    public void calculateDay(Blinkt blinkt) {
        this.setRaining(weather.getRainToday(), blinkt);
    }

    @Override
    public void update(final Blinkt blink, final float delta) {
        if (raining) {
            for (int i = 0; i < 6; ++i) {
                RAIN_EFFECT.update(i, blink, delta);
            }
        }
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

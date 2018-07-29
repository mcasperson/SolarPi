package com.matthewcasperson.displays.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.datasources.Weather;
import com.matthewcasperson.datasources.impl.WeatherImpl;
import com.matthewcasperson.displays.Display;
import com.matthewcasperson.effects.impl.SparkleEffect;

import static com.matthewcasperson.Constants.MAX_BRIGHTNESS;

public class RainDisplay implements Display {
    private static final float RAIN_CYCLE_EFFECT_PERIOD = 6.0f;
    private static final SparkleEffect RAIN_EFFECT = new SparkleEffect(MAX_BRIGHTNESS, RAIN_CYCLE_EFFECT_PERIOD);
    private final Weather weather = new WeatherImpl();
    private boolean raining = false;

    private void setRaining(final boolean raining, final Blinkt blinkt) {
        if (raining) {
            // offset the brightness so the sparkle effect isn't just all the leds
            // glowing at the same time.
            if (!this.raining) {
                for (int i = 0; i < 6; ++i) {
                    blinkt.setPixel(i, (float)Math.random() * MAX_BRIGHTNESS);
                }
            }
        } else {
            // reset the brightness
            for (int i = 0; i < 6; ++i) {
                blinkt.setPixel(i, MAX_BRIGHTNESS);
            }
        }

        this.raining = raining;
        System.out.println("Rain forecast today: " + this.raining);
    }

    @Override
    public void init(final Blinkt blinkt) {

    }

    @Override
    public void calculateMinute(final Blinkt blinkt) {

    }

    @Override
    public void calculateDay(final Blinkt blinkt) {
        this.setRaining(weather.getRainToday(), blinkt);
    }

    @Override
    public void update(final Blinkt blink, float delta) {
        if (raining) {
            for (int i = 0; i < 6; ++i) {
                RAIN_EFFECT.update(i, blink, delta);
            }
        }
    }
}

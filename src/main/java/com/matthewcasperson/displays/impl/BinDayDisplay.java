package com.matthewcasperson.displays.impl;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.displays.Display;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Shows two blue lights when the next bin day is a double bin day, and one blue light when it is a single bin day.
 */
public class BinDayDisplay implements Display {
    /**
     * A double bin day at some point in the past
     */
    private static final DateTime DOUBLE_BIN_DAY = new DateTime(2018, 7, 24, 0, 0);

    @Override
    public void init(final Blinkt blinkt) {

    }

    @Override
    public void calculateMinute(final Blinkt blinkt) {
        calculateDay(blinkt);
    }

    @Override
    public void calculateDay(Blinkt blinkt) {
        final DateTime now = new DateTime();
        blinkt.setPixel(7, 0, 0, 255);
        if (isRecyclingBinDay(now)) {
            blinkt.setPixel(6, 0, 0, 255);
        }
    }

    @Override
    public void update(final Blinkt blinkt, final float delta) {
        calculateDay(blinkt);
    }

    public boolean isRecyclingBinDay(final DateTime now) {
        final int daysSinceADoubleBinDay = Days.daysBetween(DOUBLE_BIN_DAY, now).getDays();
        return daysSinceADoubleBinDay % 14 == 0 || daysSinceADoubleBinDay % 14 > 7;
    }
}

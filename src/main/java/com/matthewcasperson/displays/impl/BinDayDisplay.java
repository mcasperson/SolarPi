package com.matthewcasperson.displays.impl;

import com.matthewcasperson.Blinkt;
import com.matthewcasperson.displays.Display;
import org.joda.time.DateTime;
import org.joda.time.Days;

public class BinDayDisplay implements Display {
    private static final DateTime DOUBLE_BIN_DAY = new DateTime(2018, 7, 24, 0, 0);

    @Override
    public void display(final Blinkt blinkt) {
        final DateTime now = new DateTime();
        //if (now.getDayOfWeek() == DateTimeConstants.SATURDAY) {
        blinkt.setPixel(7, 0, 0, 255);
        if (isRecyclingBinDay(now)) {
            blinkt.setPixel(6, 0, 0, 255);
        }
        //}
    }

    public boolean isRecyclingBinDay(final DateTime now) {
        final int daysSinceADoubleBinDay = Days.daysBetween(DOUBLE_BIN_DAY, now).getDays();
        return daysSinceADoubleBinDay % 14 == 0 || daysSinceADoubleBinDay % 14 > 7;
    }
}

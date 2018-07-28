package com.matthewcasperson.displays;

import com.matthewcasperson.displays.impl.BinDayDisplay;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class BinDayDisplayTest {
    private static final BinDayDisplay BIN_DAY_DISPLAY = new BinDayDisplay();

    @Test
    public void TestBinDay() {
        // A saturday before a single bin tuesday
        Assert.assertFalse(BIN_DAY_DISPLAY.isRecyclingBinDay(new DateTime(2018, 7, 28, 0, 0)));
        // A single bin tuesday
        Assert.assertFalse(BIN_DAY_DISPLAY.isRecyclingBinDay(new DateTime(2018, 7, 31, 0, 0)));
        // The day after a single bin tuesday
        Assert.assertTrue(BIN_DAY_DISPLAY.isRecyclingBinDay(new DateTime(2018, 8, 1, 0, 0)));
        // A double bin tuesday
        Assert.assertTrue(BIN_DAY_DISPLAY.isRecyclingBinDay(new DateTime(2018, 8, 7, 0, 0)));
        // The day after a double bin tuesday
        Assert.assertFalse(BIN_DAY_DISPLAY.isRecyclingBinDay(new DateTime(2018, 8, 8, 0, 0)));
    }
}

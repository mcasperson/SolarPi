package com.matthewcasperson;

import com.matthewcasperson.blinkt.Blinkt;
import com.matthewcasperson.blinkt.impl.BlinktImpl;
import com.matthewcasperson.displays.Display;
import com.matthewcasperson.displays.impl.BinDayDisplay;
import com.matthewcasperson.displays.impl.RainDisplay;
import com.matthewcasperson.displays.impl.SolarDisplay;
import com.matthewcasperson.utils.GeneralUtils;
import com.matthewcasperson.utils.impl.GeneralUtilsImpl;

import static com.matthewcasperson.Constants.MAX_BRIGHTNESS;
import static com.matthewcasperson.Constants.MAX_FRAME_RATE;

public class SolarPi {

    private static final GeneralUtils GENERAL_UTILS = new GeneralUtilsImpl();
    private static final int REFRESH_PERIOD = 60000;
    private static final int INITIAL_TEST_PERIOD = 1000;

    private static final Blinkt BLINKT = new BlinktImpl();
    private static final Display[] DISPLAYS = new Display[] {
            new SolarDisplay(),
            new BinDayDisplay(),
            new RainDisplay()
    };


    public static void main(final String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                BLINKT.exit();
            }
        });

        new SolarPi();
    }

    public SolarPi() {
        BLINKT.setBrightness(MAX_BRIGHTNESS);

        initialLedTest();

        for(final Display display : DISPLAYS) {
            display.init(BLINKT);
        }

        /*
            Really shouldn't be relying on a daily restart here.
         */
        for(final Display display : DISPLAYS) {
            display.calculateDay(BLINKT);
        }

        while (true) {
            displayLoop();
            updateLoop();
        }
    }

    private void displayLoop() {
        for(final Display display : DISPLAYS) {
            display.calculateMinute(BLINKT);
        }
        BLINKT.show();
    }

    private void updateLoop() {
        final long start = System.currentTimeMillis();
        long last = start;
        while (System.currentTimeMillis() - start < REFRESH_PERIOD) {
            final long delta = System.currentTimeMillis() - last;
            last += delta;

            // Limit the "frame rate".
            if (delta < MAX_FRAME_RATE) {
                GENERAL_UTILS.sleep(MAX_FRAME_RATE - delta);
            } else {
                final long fixedDelta = Math.max(MAX_FRAME_RATE, delta);

                for(final Display display : DISPLAYS) {
                    display.update(BLINKT, fixedDelta / 1000.0f);
                }
                BLINKT.show();
            }
        }
    }

    /**
     * A boot up sequence that cycles through the leds.
     */
    private void initialLedTest() {

        BLINKT.setAll(255 , 0, 0);
        BLINKT.show();
        GENERAL_UTILS.sleep(INITIAL_TEST_PERIOD);

        BLINKT.setAll(0 , 255, 0);
        BLINKT.show();
        GENERAL_UTILS.sleep(INITIAL_TEST_PERIOD);

        BLINKT.setAll(0 , 0, 255);
        BLINKT.show();
        GENERAL_UTILS.sleep(INITIAL_TEST_PERIOD);

        BLINKT.clear();
        BLINKT.show();
    }
}



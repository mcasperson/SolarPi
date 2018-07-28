package com.matthewcasperson;

import com.matthewcasperson.displays.Display;
import com.matthewcasperson.displays.impl.BinDayDisplay;
import com.matthewcasperson.displays.impl.SolarDisplay;
import com.matthewcasperson.utils.GeneralUtils;
import com.matthewcasperson.utils.impl.GeneralUtilsImpl;

public class SolarPi {

    private static final GeneralUtils GENERAL_UTILS = new GeneralUtilsImpl();
    private static final int REFRESH_PERIOD = 60000;
    private static final int INITIAL_TEST_PERIOD = 1000;
    private static final float BRIGHTNESS = 0.1f;
    private static final Blinkt BLINKT = new Blinkt();
    private static final Display[] DISPLAYS = new Display[] {
            new SolarDisplay(),
            new BinDayDisplay()
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
        BLINKT.setBrightness(BRIGHTNESS);

        initialLedTest();

        while (true) {
            for(final Display display : DISPLAYS) {
                display.display(BLINKT);
            }
            GENERAL_UTILS.sleep(REFRESH_PERIOD);
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



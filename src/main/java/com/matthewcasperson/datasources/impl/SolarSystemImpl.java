package com.matthewcasperson.datasources.impl;

import com.matthewcasperson.datasources.SolarSystem;
import com.matthewcasperson.utils.ConfigurationUtils;
import com.matthewcasperson.utils.WebUtils;
import com.matthewcasperson.utils.impl.ConfigurationUtilsImpl;
import com.matthewcasperson.utils.impl.WebUtilsImpl;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;

import static com.matthewcasperson.Constants.DATE_FORMAT;

public class SolarSystemImpl implements SolarSystem {
    private static final ConfigurationUtils CONFIGURATION_UTILS = new ConfigurationUtilsImpl();
    private static final String CURRENT_OUTPUT_ELEMENT = "tr.tr1:nth-child(5) > td:nth-child(2)";
    private static final String SOLAR_USER = "SOLAR_USER";
    private static final String SOLAR_PASS = "SOLAR_PASS";
    private static final String SOLAR_URL = "SOLAR_URL";
    private static final WebUtils WEB_UTILS = new WebUtilsImpl();
    /**
     * The solar system only updates every 5 minutes anywy, so allow a few concecutive failures
     * before resorting to a restart.
     */
    private static final int MAX_FAILURES = 10;
    private int failureCount;

    /**
     * Query the solar web page, read the output HTML, and parse it.
     * @return The solar output in watts
     */
    public float getWatts() {
        final String status = getSolarWebPage();
        final String output = parseHtmlResult(status);

        final float value = NumberUtils.toFloat(output.replaceAll("[^0-9.]*", ""), -1);
        if  (output.endsWith("kW")) {
            return value * 1000;
        }

        if  (output.endsWith("W")) {
            return value;
        }

        return -1;
    }

    /**
     * Log into the solar web page and return the raw HTML
     * @return The raw solar status page HTML
     */
    private String getSolarWebPage()  {
        try {
            return WEB_UTILS.HttpGet(
                    CONFIGURATION_UTILS.getConfigValue(SOLAR_URL),
                    CONFIGURATION_UTILS.getConfigValue(SOLAR_USER),
                    CONFIGURATION_UTILS.getConfigValue(SOLAR_PASS)
            );
        } catch (final Exception ex) {
            /*
                Successive failures will exit the application, and use supervisord to restart it
             */
            ++failureCount;
            if (failureCount >= MAX_FAILURES) {

                System.err.println(DATE_FORMAT.format(new Date()) + " Failure count exceeded, exiting");
                System.exit(1);
            }

            /*
                Any issues with the HTTP request are logged, and we return a blank string.
                This will result in the blinking red light calculateMinute.
             */
            System.err.println(DATE_FORMAT.format(new Date()) + " " + ex.toString());
            return "";
        }
    }

    /**
     * Extract the solar output from the raw HTML
     * @param value The raw HTML
     * @return The solar output
     */
    private String parseHtmlResult(final String value) {
        final Document doc = Jsoup.parse(value);
        return doc.select(CURRENT_OUTPUT_ELEMENT).text().trim();
    }
}

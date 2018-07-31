package com.matthewcasperson.datasources.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.matthewcasperson.datasources.Fuel;
import com.matthewcasperson.utils.Retry;
import com.matthewcasperson.utils.WebUtils;
import com.matthewcasperson.utils.impl.RetryImpl;
import com.matthewcasperson.utils.impl.WebUtilsImpl;
import org.joda.time.DateTime;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

public class FuelImpl implements Fuel {
    private static final WebUtils WEB_UTILS = new WebUtilsImpl();
    private static final Retry<Boolean, Exception> RETRY = new RetryImpl<>();
    private static final String URL = "https://api-01-04.motormouth.com.au/GetChartData?appToken=EE242BF3-7D54-4783-A1D9-DA1E0E403F17&userToken=&isFuelId=2&cityId=1";

    public boolean isGoodFuelDay() {
        try {
            return RETRY.retry(new RetryCallback<Boolean, Exception>() {
                @Override
                public Boolean doWithRetry(RetryContext context) throws Exception {
                    final String response = WEB_UTILS.HttpGet(URL);
                    final JsonArray predictions = new JsonParser().parse(response)
                            .getAsJsonObject()
                            .getAsJsonArray("Forecast");

                    DateTime smallestDate = null;
                    int smallestValue = 0;
                    for (int i = 0; i < predictions.size(); ++i) {
                        final DateTime date = DateTime.parse(predictions.get(i).getAsJsonObject().get("DateTimeLocal").getAsString());
                        if (smallestDate == null || date.isBefore(smallestDate)) {
                            smallestDate = date;
                            smallestValue = predictions.get(i).getAsJsonObject().get("Value").getAsInt();
                        }
                    }

                    return smallestValue == 1;
                }
            });
        } catch (final Throwable ex) {
            System.err.println(ex);
            return false;
        }
    }
}

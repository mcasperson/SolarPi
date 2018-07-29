package com.matthewcasperson.datasources.impl;

import com.matthewcasperson.datasources.Weather;

public class AlwaysRainWeather implements Weather {
    @Override
    public boolean getRainToday() {
        return true;
    }
}

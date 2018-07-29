package com.matthewcasperson.datasources.impl;

import com.matthewcasperson.datasources.Fuel;

public class AlwaysGoodFuelImpl implements Fuel {
    @Override
    public boolean isGoodFuelDay() {
        return true;
    }
}

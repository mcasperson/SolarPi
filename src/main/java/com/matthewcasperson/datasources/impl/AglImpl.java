package com.matthewcasperson.datasources.impl;

import com.matthewcasperson.datasources.Agl;
import com.matthewcasperson.datasources.EnergyRatio;

public class AglImpl implements Agl {
    @Override
    public EnergyRatio getEnergyRatio() {
        return EnergyRatio.NEGATIVE_COST;
    }
}

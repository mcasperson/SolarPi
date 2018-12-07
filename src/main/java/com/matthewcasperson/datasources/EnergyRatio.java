package com.matthewcasperson.datasources;

/**
 * Represents how much energy has been put back.
 */
public enum EnergyRatio {
    /**
     * Enough energy has been put back to result in a payment rather than a cost
     */
    NEGATIVE_COST,
    /**
     * More energy has been put back than used, but not enough to result in a payment
     */
    NEGATIVE_USE,
    /**
     * More energy was used than put back
     */
    POSITIVE_USE
}

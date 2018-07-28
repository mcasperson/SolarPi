package com.matthewcasperson.utils;

public interface GeneralUtils {
    /**
     * A no-throw sleep method
     * @param milliseconds How long to sleep for
     */
    void sleep(final int milliseconds);
    /**
     * A no-throw sleep method
     * @param milliseconds How long to sleep for
     * @param nanoseconds Nanoseconds to sleep for
     */
    void sleep(final int milliseconds, final int nanoseconds);
}

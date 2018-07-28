package com.matthewcasperson.utils;

public interface GeneralUtils {
    /**
     * A no-throw sleep method
     * @param milliseconds How long to sleep for
     */
    void sleep(final long milliseconds);
    /**
     * A no-throw sleep method
     * @param milliseconds How long to sleep for
     * @param nanoseconds Nanoseconds to sleep for
     */
    void sleep(final long milliseconds, final int nanoseconds);
}

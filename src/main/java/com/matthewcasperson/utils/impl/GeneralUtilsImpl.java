package com.matthewcasperson.utils.impl;

import com.matthewcasperson.utils.GeneralUtils;

public class GeneralUtilsImpl implements GeneralUtils {
    @Override
    public void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            // ignored
        }
    }

    @Override
    public void sleep(final long milliseconds, final int nanoseconds) {
        try {
            Thread.sleep(milliseconds, nanoseconds);
        } catch (final InterruptedException e) {
            // ignored
        }
    }
}

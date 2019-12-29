package com.simplelib.helper;

import java.util.concurrent.atomic.AtomicLong;

public final class TimeHelper {
    public static final long NO_TIME = -1L;

    private TimeHelper() {
    }

    public static float getDelta(AtomicLong lastTimeInMillisHolder, float unitsPerSecond) {
        return (float) getDelta(lastTimeInMillisHolder, (double) unitsPerSecond);
    }

    public static double getDelta(AtomicLong lastTimeInMillisHolder, double unitsPerSecond) {
        if (lastTimeInMillisHolder == null)
            throw new NullPointerException("No value holder attached");
        long lastTimeInMillis = lastTimeInMillisHolder.get();
        long timeInMillis = getTimeInMillis();
        lastTimeInMillisHolder.set(timeInMillis);
        return getDelta(timeInMillis, lastTimeInMillis, unitsPerSecond);
    }

    public static float getDelta(long timeInMillis, long lastTimeInMillis, float unitsPerSecond) {
        return (float) getDelta(timeInMillis, lastTimeInMillis, (double) unitsPerSecond);
    }

    public static double getDelta(long timeInMillis, long lastTimeInMillis, double unitsPerSecond) {
        if (timeInMillis == NO_TIME || lastTimeInMillis == NO_TIME) {
            return 0d;
        }
        long deltaTimeInMillis = timeInMillis - lastTimeInMillis;
        if (deltaTimeInMillis < 0L) {
            return 0d;
        }
        return unitsPerSecond * ((double) deltaTimeInMillis / 1000d);
    }

    public static long getTimeInMillis() {
        return System.currentTimeMillis();
    }
}

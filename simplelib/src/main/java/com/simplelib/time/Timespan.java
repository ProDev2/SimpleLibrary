package com.simplelib.time;

public class Timespan {
    private Time from;
    private Time to;

    public Timespan(Time from, Time to) {
        this.from = from;
        this.to = to;
    }

    public static Timespan create(Time from, Time to) {
        return new Timespan(from, to);
    }

    public Time getFrom() {
        return from;
    }

    public Time getTo() {
        return to;
    }

    public boolean isInbetween() {
        return isInbetween(Time.create());
    }

    public boolean isInbetween(Time time) {
        long fromTime = from.getAsMillis();
        long toTime = to.getAsMillis();

        long millis = time.getAsMillis();

        return millis >= fromTime && millis <= toTime;
    }

    public float getProgress() {
        return getProgress(Time.create());
    }

    public float getProgress(Time time) {
        if (isInbetween()) {
            float max = getDifference();
            float pos = getDifference(from, time);

            if (!(pos == 0 || max == 0))
                return pos / (max / 100);
        }
        return 0;
    }

    public long getDifference() {
        return getDifference(from, to);
    }

    public long getDifference(Time from, Time to) {
        long fromTime = from.getAsMillis();
        long toTime = to.getAsMillis();

        return Math.max(fromTime, toTime) - Math.min(fromTime, toTime);
    }
}
package org.ijsberg.iglu.util.time;

public class TimePeriod {
    private int length;
    private TimeUnit timeUnit;

    public TimePeriod(int length, TimeUnit timeUnit) {
        this.length = length;
        this.timeUnit = timeUnit;
    }

    public int getLength() {
        return length;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public boolean hasFixedDuration() {
        return timeUnit.hasFixedDuration();
    }

    public long getDurationInMs() {
        return length * timeUnit.getDurationInMs();
    }
}

package org.ijsberg.iglu.util.time;

import java.time.temporal.TemporalUnit;
import java.util.Calendar;

import static java.time.temporal.ChronoUnit.*;
import static java.util.Calendar.*;

public enum TimeUnit {
    INDEFINITE(0, FOREVER, 0),
    SECOND(Calendar.SECOND, SECONDS, 1000),
    MINUTE(Calendar.MINUTE, MINUTES, 1000 * 60),
    HOUR(HOUR_OF_DAY, HOURS, 1000 * 60 * 60),
    DAY(DATE, DAYS, 1000 * 60 * 60 * 24),
    MONTH(Calendar.MONTH, MONTHS, 0),
    YEAR(Calendar.YEAR, YEARS, 0),
    WEEK(WEEK_OF_YEAR, WEEKS, 1000 * 60 * 60 * 24 * 7);

    private int calendarConstant;
    private TemporalUnit temporalUnit;
    private long durationInMs;

    TimeUnit(int calendarConstant, TemporalUnit temporalUnit, long durationInMs) {
        this.calendarConstant = calendarConstant;
        this.temporalUnit = temporalUnit;
        this.durationInMs = durationInMs;
    }

    boolean hasFixedDuration() {
        return durationInMs > 0;
    }

    int getCalendarConstant() {
        return calendarConstant;
    }

    public TemporalUnit getTemporalUnit() {
        return temporalUnit;
    }

    public long getDurationInMs() {
        if(!hasFixedDuration()) {
            throw new TimeUnitException("TimeUnit has no fixed duration");
        }
        return durationInMs;
    }
}

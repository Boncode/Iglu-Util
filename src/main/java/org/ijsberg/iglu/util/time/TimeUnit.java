package org.ijsberg.iglu.util.time;

import java.time.temporal.TemporalUnit;
import java.util.Calendar;

import static java.time.temporal.ChronoUnit.*;
import static java.util.Calendar.*;

public enum TimeUnit {
    NONE(0, FOREVER),
    MINUTE(Calendar.MINUTE, MINUTES),
    HOUR(HOUR_OF_DAY, HOURS),
    DAY(DATE, DAYS),
    MONTH(Calendar.MONTH, MONTHS),
    YEAR(Calendar.YEAR, YEARS),
    WEEK(WEEK_OF_YEAR, WEEKS);

    private int calendarConstant;
    private TemporalUnit temporalUnit;

    TimeUnit(int calendarConstant, TemporalUnit temporalUnit) {
        this.calendarConstant = calendarConstant;
        this.temporalUnit = temporalUnit;
    }

    int getCalendarConstant() {
        return calendarConstant;
    }

    public TemporalUnit getTemporalUnit() {
        return temporalUnit;
    }
}

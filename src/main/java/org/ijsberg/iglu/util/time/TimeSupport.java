/*
 * Copyright 2011-2014 Jeroen Meetsma - IJsberg Automatisering BV
 *
 * This file is part of Iglu.
 *
 * Iglu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iglu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Iglu.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ijsberg.iglu.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static java.util.Calendar.DATE;

/**
 * Contains convenience methods concerning time, date and scheduling.
 */
public abstract class TimeSupport {

    public static final String TIMESTAMP_FORMAT_EXCEL = "yyyy-MM-dd HH:mm";

    public static Date makeDate(int year, int month, int day) {
		return makeDate(year, month, day, 0, 0);
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute) {
		Calendar cal = new GregorianCalendar();
		cal.set(year, month - 1, day, hour, minute);
		return cal.getTime();
	}

	public static Date getTimeStampExcel(String timestampStr) throws ParseException {
		timestampStr = timestampStr.replaceAll("T"," ");
		return new SimpleDateFormat(TIMESTAMP_FORMAT_EXCEL).parse(timestampStr);
	}

	public static String getTimeStampExcel(Date timestamp) {
		return new SimpleDateFormat(TIMESTAMP_FORMAT_EXCEL).format(timestamp);
	}

	public static final int SECOND_IN_MS = 1000;
	public static final int MINUTE_IN_MS = 60 * SECOND_IN_MS;
	public static final int HALF_MINUTE_IN_MS = 30 * SECOND_IN_MS;
	public static final int HOUR_IN_MS = 60 * MINUTE_IN_MS;
	public static final int DAY_IN_MS = 24 * HOUR_IN_MS;
	public static final int DAY_IN_MINS = 24 * 60;

	public static final long LOCAL_UTC_OFFSET = getLocalUtcOffset();
	public static final long LOCAL_UTC_OFFSET_IN_MINUTES = LOCAL_UTC_OFFSET / MINUTE_IN_MS;

	/**
	 * The offset from UTC can be used to calculate time and date of time stamps based on
	 * <code>System.currentTimeInMillis()</code>.
	 * 
	 * @return offset from UTC in milliseconds
	 */
	private static long getLocalUtcOffset() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		return getUtcOffset(cal);
	}

	/**
	 * @return offset from UTC in milliseconds
	 */
	public static long getUtcOffset(Calendar cal) {
		//offset from UTC consists of daylight saving time and time zone offset
		return cal.get(Calendar.DST_OFFSET) + cal.get(Calendar.ZONE_OFFSET);
	}

	/**
	 * @param date
	 * @return true if the given date happens to be today
	 */
	public static boolean isToday(Date date) {
		Calendar calInput = new GregorianCalendar();
		calInput.setTime(date);

		Calendar calToday = new GregorianCalendar();
		calToday.setTime(new Date());

		return calInput.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR) &&
				calInput.get(Calendar.YEAR) == calToday.get(Calendar.YEAR);
	}

	/**
	 * @param date
	 * @return true if the given date is on a day earlier than today
	 */
	public static boolean isBeforeToday(Date date) {
		Calendar calInput = new GregorianCalendar();
		calInput.setTime(date);

		Calendar calToday = new GregorianCalendar();
		calToday.setTime(new Date());

		//it doesn't matter if you multiply nr of years with 365.25 or 1000
		return (calInput.get(Calendar.YEAR) * 1000 + calInput.get(Calendar.DAY_OF_YEAR)) < (calToday.get(Calendar.YEAR) * 1000 + calToday.get(Calendar.DAY_OF_YEAR));
	}


	/**
	 * @param date
	 * @return true if the given date is on a day later than today
	 */
	public static boolean isAfterToday(Date date) {
		Calendar calInput = new GregorianCalendar();
		calInput.setTime(date);

		Calendar calToday = new GregorianCalendar();
		calToday.setTime(new Date());

		//it doesn't make any difference if you multiply nr of years with 365.25 or 1000
		return (calInput.get(Calendar.YEAR) * 1000 + calInput.get(Calendar.DAY_OF_YEAR)) > (calToday.get(Calendar.YEAR) * 1000 + calToday.get(Calendar.DAY_OF_YEAR));
	}


	/**
	 * @param time1 time in millis
	 * @param time2 time in millis
	 * @return true if the two specified times are part of the same day
	 */
	public static boolean isSameDay(long time1, long time2) {
		return isSameDay(new Date(time1), new Date(time2));
	}

	/**
	 * @param date1
	 * @param date2
	 * @return true if the two specified dates are part of the same day
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);
		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.setTime(date2);

		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * @param time1 time in millis
	 * @param time2 time in millis
	 * @return true if the two specified times are part of the same week
	 */
	public static boolean isSameWeek(long time1, long time2) {
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(new Date(time1));
		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.setTime(new Date(time2));

		return (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR));
	}
	
	/**
	 * @param time time in date format
	 * @return time rounded to the minute
	 */
	public static long roundToMinute(long time) {
		return MINUTE_IN_MS * ((time + HALF_MINUTE_IN_MS) / MINUTE_IN_MS);
	}

	/**
	 * @param time time in millis
	 * @return time in milles rounded to the minute
	 */
	public static Date roundToMinute(Date time) {
		return new Date(MINUTE_IN_MS * ((time.getTime() + HALF_MINUTE_IN_MS) / MINUTE_IN_MS));
	}

	/**
	 * @return The number of minutes passed since 00:00 hours
	 */
	public static int getMinutesSinceMidnight() {
		return getMinutesSinceMidnight(System.currentTimeMillis());
	}

	/**
	 * @param time
	 * @return the number of minutes passed since 00:00 hours in current time zone
	 */
	public static int getMinutesSinceMidnight(long time) {
		return SchedulingSupport.getIntervalsSinceMidnight(time, 1);
	}

	public static Date floorToMidnight(Date date) {

		Calendar calInput = new GregorianCalendar();
		calInput.setTime(date);

		Calendar calOutput = new GregorianCalendar(calInput.get(Calendar.YEAR), calInput.get(Calendar.MONTH), calInput.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

		return calOutput.getTime();
	}

	public static List<Date> getDaysInBetween(Date periodStart, Date periodEnd) {
		List<Date> datesInBetween = new ArrayList<>();
		Date possibleDateInBetween = getNextDay(periodStart);
		while(possibleDateInBetween.before(periodEnd)) {
			datesInBetween.add(possibleDateInBetween);
			possibleDateInBetween = getNextDay(possibleDateInBetween);
		}
		return datesInBetween;
	}

	public static Date nowMinus(long differenceInMillis) {
		return new Date(System.currentTimeMillis() - differenceInMillis);
	}

	public static Date getNextDay(Date date) {
		return getDifferentDay(date, 1);
	}

	public static Date getDifferentDay(Date date, int nrOfDaysOffSet) {
		Calendar calInput = new GregorianCalendar();
		calInput.setTime(date);
		calInput.add(DATE, nrOfDaysOffSet);
		return calInput.getTime();
	}

	public static Date getDateAfterNrOfDays(Date date, int nrOfDays) {
		Date nextDate = date;
		for(int i = 0; i < nrOfDays; i++) {
			nextDate = getNextDay(nextDate);
		}
		return nextDate;
	}

	public static Date convertToDateFromISO_8601(String timestamp) {
		Date d = Date.from(convertFromISO_8601(timestamp));
		return d;
	}

	public static Instant convertFromISO_8601(String timestamp) {
		TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(timestamp);
		Instant i = Instant.from(ta);
		return i;
	}

	public static Date getUpcomingDate(Date initialDate, int nrTimeUnits, TimeUnit timeUnit) {
		return getUpcomingDate(new Date(), initialDate, nrTimeUnits, timeUnit);
	}
	public static Date getUpcomingDate(Date referenceDate, Date initialDate, int nrTimeUnits, TimeUnit timeUnit) {

		if(nrTimeUnits <= 0) {
			throw new IllegalArgumentException("nr of timeunits (" + nrTimeUnits + ") should be larger than 0");
		}
		Date nextDate = initialDate;
		Calendar calendar = new GregorianCalendar();
		while(referenceDate.after(nextDate)) {
			calendar.setTime(nextDate);
			calendar.add(timeUnit.getCalendarConstant(), nrTimeUnits);
			nextDate = calendar.getTime();
		}
		return nextDate;
	}
	public static Date getPreviousDate(Date initialDate, int nrTimeUnits, TimeUnit timeUnit) {
		return getPreviousDate(new Date(), initialDate, nrTimeUnits, timeUnit);
	}

	public static Date getPreviousDate(Date referenceDate, Date initialDate, int nrTimeUnits, TimeUnit timeUnit) {
		if(nrTimeUnits <= 0) {
			throw new IllegalArgumentException("nr of timeunits (" + nrTimeUnits + ") should be larger than 0");
		}
		Date previousDate = initialDate;
		Calendar calendar = new GregorianCalendar();
		while(referenceDate.before(previousDate)) {
			calendar.setTime(previousDate);
			calendar.add(timeUnit.getCalendarConstant(), (-1 * nrTimeUnits));
			previousDate = calendar.getTime();
		}
		return previousDate;
	}

}

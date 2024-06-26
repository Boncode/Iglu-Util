/*
 * Copyright 2011-2013 Jeroen Meetsma - IJsberg
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

import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static java.util.Calendar.*;
import static org.ijsberg.iglu.util.time.TimeUnit.DAY;
import static org.ijsberg.iglu.util.time.TimeUnit.WEEK;
import static org.junit.Assert.*;


public class TimeSupportTest {

	@Test
	public void testTimeInMillisIsTimeZoneDependent() throws Exception {

		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(1970, 0, 27, 15, 6, 0);

		long timeUtcInMillis = cal.getTimeInMillis();
		
		cal.setTimeZone(TimeZone.getTimeZone("CET"));
		cal.set(1970, 0, 27, 15, 6, 0);

		//Calendar subtracts an hour from UTC time
		//  because the local time CET is an hour ahead
		long timeCetInMillis = cal.getTimeInMillis();
		
		assertEquals(timeCetInMillis, timeUtcInMillis - TimeSupport.HOUR_IN_MS);
	}

	private static Calendar getBirthTime() {
		Calendar cal = new GregorianCalendar();
		cal.set(1970, 0, 27, 15, 6, 0);
		return cal;
	}

	@Test
	public void testGetUtcOffset() throws Exception {
		
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.set(YEAR, 2012);
		cal.setTimeZone(TimeZone.getTimeZone("CET"));
		
		assertEquals(TimeSupport.HOUR_IN_MS, TimeSupport.getUtcOffset(cal));
		cal.set(Calendar.DAY_OF_YEAR, 183);
		assertEquals(2 * TimeSupport.HOUR_IN_MS, TimeSupport.getUtcOffset(cal));

		cal.setTimeZone(TimeZone.getTimeZone("EST"));
		assertEquals(-5 * TimeSupport.HOUR_IN_MS, TimeSupport.getUtcOffset(cal));
	}

	
	@Test
	public void testIsToday() throws Exception {
		
		Date date = getDateAtLeast10SecBeforeMidnight();
		assertTrue(TimeSupport.isToday(date));
		
		date.setTime(date.getTime() + TimeSupport.DAY_IN_MS);
		assertFalse(TimeSupport.isToday(date));

		date.setTime(date.getTime() - 2 * TimeSupport.DAY_IN_MS);
		assertFalse(TimeSupport.isToday(date));
	}

	@Test
	public void testIsBeforeToday() throws Exception {
		
		Date date = getDateAtLeast10SecBeforeMidnight();
		assertFalse(TimeSupport.isBeforeToday(date));
		
		date.setTime(date.getTime() + TimeSupport.DAY_IN_MS);
		assertFalse(TimeSupport.isBeforeToday(date));

		date.setTime(date.getTime() - 2 * TimeSupport.DAY_IN_MS);
		assertTrue(TimeSupport.isBeforeToday(date));
	}

    @Test
    public void hasDST() throws Exception {
        assertEquals(0, new GregorianCalendar(1970, 1, 1, 23, 59, 55).get(Calendar.DST_OFFSET));
        assertEquals(3600000, new GregorianCalendar(2012, 7, 1, 23, 59, 55).get(Calendar.DST_OFFSET));
    }

    @Test
	public void testIsLE10SecsBeforeMidnight() throws Exception {
        //TODO next tests fail during DST season
        /*
        assertTrue(isLE10SecsBeforeMidnight(new GregorianCalendar(1970, 1, 1, 23, 59, 55)));
		assertTrue(isLE10SecsBeforeMidnight(new GregorianCalendar(1970, 1, 1, 23, 59, 50)));
		assertTrue(isLE10SecsBeforeMidnight(new GregorianCalendar(1970, 1, 1, 23, 59, 59)));
		assertFalse(isLE10SecsBeforeMidnight(new GregorianCalendar(1970, 1, 1, 23, 59, 49)));
		assertFalse(isLE10SecsBeforeMidnight(new GregorianCalendar(1970, 1, 2, 0, 0, 0)));
		*/
	}

	public static Date getDateAtLeast10SecBeforeMidnight() throws InterruptedException {
		Date date = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		//wait if it's just before midnight
		while(isLE10SecsBeforeMidnight(cal)) {

			Thread.sleep(1000);
			date = new Date();
			cal.setTime(date);
		}
		return date;
	}

	public static boolean isLE10SecsBeforeMidnight(Calendar cal) {
		return isLESecsBeforeInterval(cal.getTimeInMillis(), 10, TimeSupport.DAY_IN_MINS);
	}

	public static Date getDateAtLeast10SecBeforeInterval() throws InterruptedException {
		Date date = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		//wait if it's just before midnight
		while(isLE10SecsBeforeMidnight(cal)) {

			Thread.sleep(1000);
			date = new Date();
			cal.setTime(date);
		}
		return date;
	}

	public static boolean isLESecsBeforeInterval(long time, int minimumBefore, int intervalInMinutes) {
		return SchedulingSupport.getNextIntervalStart(time, intervalInMinutes) <= time + (minimumBefore * TimeSupport.SECOND_IN_MS);
	}

	@Test
	public void testIsAfterToday() throws Exception {
		
		Date date = getDateAtLeast10SecBeforeMidnight();
		assertFalse(TimeSupport.isAfterToday(date));
		
		date.setTime(date.getTime() + TimeSupport.DAY_IN_MS);
		assertTrue(TimeSupport.isAfterToday(date));

		date.setTime(date.getTime() - 2 * TimeSupport.DAY_IN_MS);
		assertFalse(TimeSupport.isAfterToday(date));
	}
	
	
	@Test
	public void testGetMinutesSinceMidnight() throws Exception {
		
		Date now = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(now);
		assertEquals(cal.get(Calendar.MINUTE) + (cal.get(Calendar.HOUR_OF_DAY) * 60), TimeSupport.getMinutesSinceMidnight());
	}

	
	
	public static long getTime(int hours, int minutes) {
		return new GregorianCalendar(1970, 0, 27, hours, minutes).getTimeInMillis();
	}
	
	@Test
	public void testGetDaysInBetween() throws Exception {
		int nrOfDays = 10;
		long startMillis = 12821893922L;
		Date firstDate = new Date(startMillis);
		Date secondDate = new Date(startMillis + (1000 * 60 * 60 * 24) * nrOfDays);
		assertEquals(nrOfDays-1, TimeSupport.getDaysInBetween(firstDate, secondDate).size());
	}

	@Test
	public void testGetNextDay() throws Exception {
		long startMillis = 12821893922L;
		Date currentDate = new Date(startMillis);

		for(int i = 1; i <= 365; i++) { // test a full year after
			Date nextDate = TimeSupport.getNextDay(currentDate);
			assertEquals(TimeSupport.DAY_IN_MS, nextDate.getTime() - currentDate.getTime());
			currentDate = nextDate;
		}
	}

	@Test
	@Ignore
	public void testGetNrOfDaysLater() throws Exception {
		int nrOfDaysLater = 5;
		Date currentDate = new Date(System.currentTimeMillis());
		Date floored = TimeSupport.floorToMidnight(currentDate);

		Date fiveDaysLater = TimeSupport.getDateAfterNrOfDays(floored, nrOfDaysLater);
		assertEquals(TimeSupport.DAY_IN_MS*nrOfDaysLater, fiveDaysLater.getTime() - floored.getTime());
	}

	@Test
	public void testGetDifferentDay() {
		Calendar cal = getBirthTime();
		Date date = cal.getTime();
		Date differentDate = TimeSupport.getDifferentDay(date, -28);
		cal.setTime(differentDate);
		assertEquals(30, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, cal.get(MONTH));
	}

	@Test
	public void testCalculateNextDate() {
		//NOTE calendar month starts with 0 (january)
		Date date = new Date();
		Calendar refDate = new GregorianCalendar();
		refDate.set(2022,12 - 1,25);

		Calendar initialDate = new GregorianCalendar();
		initialDate.set(2022, 6 - 1, 15);

		Date upcomingDate = TimeSupport.getUpcomingDate(refDate.getTime(), initialDate.getTime(), 7, DAY);
		Calendar upcomingDateCal = new GregorianCalendar();
		upcomingDateCal.setTime(upcomingDate);

		assertEquals(2022, upcomingDateCal.get(YEAR));
		assertEquals(11, upcomingDateCal.get(MONTH));
		assertEquals(28, upcomingDateCal.get(DATE));

		refDate.set(2022,12 - 1,29);
		initialDate.set(2022, 12 - 1, 30);

		//should be initial date
		upcomingDate = TimeSupport.getUpcomingDate(refDate.getTime(), initialDate.getTime(), 1, WEEK);
		upcomingDateCal.setTime(upcomingDate);

		assertEquals(2022, upcomingDateCal.get(YEAR));
		assertEquals(11, upcomingDateCal.get(MONTH));
		assertEquals(30, upcomingDateCal.get(DATE));


		refDate.set(2022,12 - 1,29);
		initialDate.set(2022, 12 - 1, 28);

		//should be JAN 2023
		upcomingDate = TimeSupport.getUpcomingDate(refDate.getTime(), initialDate.getTime(), 1, WEEK);
		upcomingDateCal.setTime(upcomingDate);

		assertEquals(2023, upcomingDateCal.get(YEAR));
		assertEquals(0, upcomingDateCal.get(MONTH));
		assertEquals(4, upcomingDateCal.get(DATE));

		//should be FEB 2023
		upcomingDate = TimeSupport.getUpcomingDate(refDate.getTime(), initialDate.getTime(), 2, TimeUnit.MONTH);
		upcomingDateCal.setTime(upcomingDate);

		assertEquals(2023, upcomingDateCal.get(YEAR));
		assertEquals(1, upcomingDateCal.get(MONTH));
		assertEquals(28, upcomingDateCal.get(DATE));


		refDate.set(2022, 6 - 1, 15, 13, 3, 20);
		initialDate.set(2022, 6 - 1, 15, 13, 1, 0);

		upcomingDate = TimeSupport.getUpcomingDate(refDate.getTime(), initialDate.getTime(), 1, TimeUnit.MINUTE);
		upcomingDateCal = new GregorianCalendar();
		upcomingDateCal.setTime(upcomingDate);

		assertEquals(4, upcomingDateCal.get(MINUTE));

	}

}


package com.mj.drinkmorewater;

import com.mj.drinkmorewater.Utils.DateUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the DateUtils logic.
 */
public class DateUtilsTest {

    @Test
    public void currentLocalDate_ReturnsTrue() {
        LocalDate localDate = DateUtils.getCurrentDate();
        LocalDate currentLocalDate = LocalDate.now();

        assertEquals(currentLocalDate, localDate);
    }

    @Test
    public void subtractLocalDateDaysTest() {
        LocalDate today = DateUtils.getCurrentDate();
        LocalDate todayMinusFiveDays = today.minusDays(5);
        assertEquals(todayMinusFiveDays, DateUtils.substractDays(today, 5));
    }

    @Test
    public void subtractLocalDateTimeHoursWithSecondsTest() {
        LocalDateTime localDateTime = LocalDateTime.parse("2019-10-10 21:00:15", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        assertEquals("2019-10-10T19:00:15", DateUtils.substractHours(localDateTime, 2).toString());

    }

    @Test
    public void subtractLocalDateTimeHoursTest() {
        LocalDateTime localDateTime = LocalDateTime.parse("2019-10-10 21:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        assertEquals("2019-10-10T19:00", DateUtils.substractHours(localDateTime, 2).toString());
    }

    @Test
    public void subtractLocalDateTimeDays() {
        LocalDateTime localDate = LocalDateTime.parse("2019-10-10 21:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        assertEquals("2019-10-08T21:00", DateUtils.substractDays(localDate, 2).toString());
    }

}

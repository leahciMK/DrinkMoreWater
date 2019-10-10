package com.mj.drinkmorewater.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    public static final int TWO_HOURS_IN_MILIS = 7200 * 1000;
    public static final int TWO_SECONDS = 2000;

    public static final String DATE = "yyyy-MM-dd";
    public static final String DATE_AND_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String HOURS_MINUTES_SECONDS_START = "00:00:00";
    public static final String HOURS_MINUTES_SECONDS_END = "23:59:59";

    public static String localDateToString(LocalDate localDate) {
        return localDate.toString();
    }

    public static String localDateWithFormatter(LocalDate localDate, String pattern) {
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.toString();
    }

    public static String localDateTimeWithFormatter(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static LocalDateTime getCurrentDateAndTime() {
        return LocalDateTime.now();
    }

    public static String getFormattedCurrentDate() {
        return localDateWithFormatter(LocalDate.now(), DATE);
    }

    public static String getCurrentDateAndTimeToString() {
        return localDateTimeToString(LocalDateTime.now());
    }

    public static String getFormattedCurentDateAndTime() {
        return localDateTimeWithFormatter(LocalDateTime.now(), DATE_AND_TIME);
    }

    public static LocalDate substractDays(LocalDate date, long days) {
        return date.minusDays(days);
    }

    public static LocalDateTime substractDays(LocalDateTime dateTime, long days) {
        return dateTime.minusDays(days);
    }

    public static LocalDateTime substractHours(LocalDateTime dateTime, long hours) {
        return dateTime.minusHours(hours);
    }
}

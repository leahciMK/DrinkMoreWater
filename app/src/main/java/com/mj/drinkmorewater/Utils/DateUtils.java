package com.mj.drinkmorewater.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final int TWO_HOURS_IN_MILIS = 7200 * 1000;
    public static final int TWO_SECONDS = 2000;

    public static final String DATE = "yyyy-MM-dd";
    public static final String DATE_AND_TIME = "yyyy-MM-dd HH:mm:ss";

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

    public static String getFormattedCurrentDate() {
        return localDateWithFormatter(LocalDate.now(), DATE);
    }

    public static String getCurrentDateAndTime() {
        return localDateTimeToString(LocalDateTime.now());
    }

    public static String getFormattedCurentDateAndTime() {
        return localDateTimeWithFormatter(LocalDateTime.now(), DATE_AND_TIME);
    }
}

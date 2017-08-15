package com.tookbra.water.order.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by tookbra on 2017/8/7.
 */
public class DateUtil {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static LocalDate getLocalDate() {
        LocalDate today = LocalDate.now();
        return today;
    }

    public static LocalTime getLocalTime() {
        return LocalTime.now();
    }

    public static LocalTime getLocalTimeByFmt(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    public static String getDay(int days) {
        LocalDate day = getLocalDate().plusDays(days);
        return "" + day;
    }

    public static String getDay() {
        LocalDate today = LocalDate.now();
        return "" + today;
    }

    public static boolean checkTimeFormat(String time) {
        try {
            LocalTime.parse(time, timeFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static String getOrderDateStr() {
        return "" + getOrderDate();
    }

    public static LocalDate getOrderDate() {
        int week = getLocalDate().getDayOfWeek().getValue();
        if(week == 4) {
            return getLocalDate().plusDays(3);
        } else {
            return getLocalDate().plusDays(2);
        }
    }

    public static Integer getOrderWeek() {
        int week = getOrderDate().getDayOfWeek().getValue();
        if(week == 7) {
            return 0;
        } else {
            return week;
        }
    }

    public static Long getLocalDateTimeByFmt(String time) {
        return Timestamp.valueOf(LocalDateTime.parse(time, dateTimeFormatter)).getTime();
    }

    public static boolean compore(LocalTime localTime1, LocalTime localTime2) {
        if(localTime1.isAfter(localTime2)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(Timestamp.valueOf(LocalDateTime.now()).getTime());
        System.out.println(""+getLocalDateTimeByFmt("2017-08-10 01:00:00"));
    }


}

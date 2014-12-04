package ru.metal.cashflow.server.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * Create date with
     *
     * @param year       specified year
     * @param month      specified month
     * @param dayOfMonth specified day of month
     * @return date
     */
    public static Date create(int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calendar.getTime();
    }

    /**
     * Substract days from date
     *
     * @param date date
     * @param day  days
     * @return date
     */
    public static Date minusDay(Date date, int day) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.roll(Calendar.DAY_OF_YEAR, -day);

        return calendar.getTime();
    }

    /**
     * Add months to date
     *
     * @param date  date
     * @param month month to add
     * @return date
     */
    public static Date plusMonth(Date date, int month) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.roll(Calendar.MONTH, month);

        return calendar.getTime();
    }
}

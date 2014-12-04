package ru.metal.cashflow.server.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    @Test
    public void createTes() throws Exception {
        final Date date = DateUtils.create(2014, 0, 1);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(2014, calendar.get(Calendar.YEAR));
        assertEquals(0, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void minusDayTest() throws Exception {
        final Date date = DateUtils.minusDay(DateUtils.create(2014, 7, 1), 1);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(2014, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void plusMonthTest() throws Exception {
        final Date date = DateUtils.plusMonth(DateUtils.create(2014, 0, 1), 3);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(2014, calendar.get(Calendar.YEAR));
        assertEquals(3, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
    }
}

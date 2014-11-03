package ru.metal.cashflow.utils;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;

public class HibernateUtilsTest {

    /**
     * Performe the count request to database
     *
     * @param session hibernate session
     * @param clazz   type of object size we need to know
     * @return how many object in database
     */
    public static int executeCount(Session session, Class clazz) {
        final Number count = (Number) session.createCriteria(clazz).setProjection(Projections.rowCount()).uniqueResult();
        return count.intValue();
    }
}

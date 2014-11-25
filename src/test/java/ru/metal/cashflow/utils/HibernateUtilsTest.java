package ru.metal.cashflow.utils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public class HibernateUtilsTest {

    /**
     * Performe the count request to database
     *
     * @param entityManager entity manager
     * @param clazz         type of object size we need to know
     * @return how many object in database
     */
    public static long executeCount(EntityManager entityManager, Class clazz) {
        final CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        //noinspection unchecked
        cq.select(qb.count(cq.from(clazz)));
        return entityManager.createQuery(cq).getSingleResult();
    }
}

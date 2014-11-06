package ru.metal.cashflow.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Currency;

/**
 * DAO service to work with currency
 */
@Repository
public class CurrencyDAO implements CRUDService<Currency> {

    private static final Log logger = LogFactory.getLog(CurrencyDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void insert(Currency model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            session.save(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while inserting new currency", e);
            throw new CFException(e);
        }
    }

    @Override
    public void update(Currency model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            model = (Currency) session.merge(model);
            session.update(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while updating existing currency", e);
            throw new CFException(e);
        }
    }

    @Override
    public Currency get(Integer id) throws CFException {
        try {
            return (Currency) sessionFactory.getCurrentSession().get(Currency.class, id);
        } catch (HibernateException e) {
            logger.error("Error while geting existing currency from database", e);
            throw new CFException(e);
        }
    }

    @Override
    public void delete(Integer id) throws CFException {
        try {
            final Currency currency = get(id);
            final Session session = sessionFactory.getCurrentSession();
            session.delete(currency);
            session.flush();
        } catch (IllegalArgumentException | HibernateException e) {
            logger.error("Error while deleteing existing currency", e);
            throw new CFException(e);
        }
    }
}

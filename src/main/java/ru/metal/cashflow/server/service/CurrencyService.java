package ru.metal.cashflow.server.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Currency;

/**
 * Service to work with currency
 */
public class CurrencyService implements CRUDService<Currency> {

    private static final Log logger = LogFactory.getLog(CurrencyService.class);

    private SessionFactory sessionFactory;

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void insert(Currency model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            session.save(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while inserting new currency", e);
            throw new CFException("Error while inserting new currency", e);
        }
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Currency model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            model = (Currency) session.merge(model);
            session.update(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while updating existing currency", e);
            throw new CFException("Error while updating existing currency", e);
        }
    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Currency get(Integer id) throws CFException {
        try {
            return (Currency) sessionFactory.getCurrentSession().get(Currency.class, id);
        } catch (HibernateException e) {
            logger.error("Error while geting existing currency from database", e);
            throw new CFException("Error while geting existing currency from database", e);
        }
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        try {
            final Currency currency = get(id);
            final Session session = sessionFactory.getCurrentSession();
            session.delete(currency);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while deleteing existing currency", e);
            throw new CFException("Error while deleteing existing currency", e);
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

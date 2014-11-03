package ru.metal.cashflow.server.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;

/**
 * Service to work with accounts
 */
public class AccountService implements CRUDService<Account> {

    private static final Log logger = LogFactory.getLog(AccountService.class);

    private SessionFactory sessionFactory;

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void insert(Account model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            session.save(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while inserting new account", e);
            throw new CFException("Error while inserting new account", e);
        }
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Account model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            model = (Account) session.merge(model);
            session.update(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while updating existing account", e);
            throw new CFException("Error while updating existing account", e);
        }
    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Account get(Integer id) throws CFException {
        try {
            return (Account) sessionFactory.getCurrentSession().get(Account.class, id);
        } catch (HibernateException e) {
            logger.error("Error while geting existing account from database", e);
            throw new CFException("Error while geting existing account from database", e);
        }
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        try {
            final Account currency = get(id);
            final Session session = sessionFactory.getCurrentSession();
            session.delete(currency);
            session.flush();
        } catch (IllegalArgumentException | HibernateException e) {
            logger.error("Error while deleteing existing account", e);
            throw new CFException("Error while deleteing existing account", e);
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

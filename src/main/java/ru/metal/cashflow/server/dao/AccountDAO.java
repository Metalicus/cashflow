package ru.metal.cashflow.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;

import java.util.List;

/**
 * DAO service to work with accounts
 */
@Repository
public class AccountDAO {

    private static final Log logger = LogFactory.getLog(AccountDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    public List<Account> getAccounts() throws CFException {
        try {
            final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Account.class);
            //noinspection unchecked
            return criteria.list();
        } catch (HibernateException e) {
            logger.error("Error while collection accounts", e);
            throw new CFException(e);
        }
    }

    public Account insert(Account model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            session.save(model);
            session.flush();

            return model;

        } catch (HibernateException e) {
            logger.error("Error while inserting new account", e);
            throw new CFException(e);
        }
    }

    public Account update(Account model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            model = (Account) session.merge(model);
            session.update(model);
            session.flush();

            return model;

        } catch (HibernateException e) {
            logger.error("Error while updating existing account", e);
            throw new CFException(e);
        }
    }

    public Account get(Integer id) throws CFException {
        try {
            return (Account) sessionFactory.getCurrentSession().get(Account.class, id);
        } catch (HibernateException e) {
            logger.error("Error while geting existing account from database", e);
            throw new CFException(e);
        }
    }

    public void delete(Integer id) throws CFException {
        try {
            final Account account = get(id);
            final Session session = sessionFactory.getCurrentSession();
            session.delete(account);
            session.flush();
        } catch (IllegalArgumentException | HibernateException e) {
            logger.error("Error while deleteing existing account", e);
            throw new CFException(e);
        }
    }
}

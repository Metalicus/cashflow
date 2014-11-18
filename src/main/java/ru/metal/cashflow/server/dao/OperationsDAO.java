package ru.metal.cashflow.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Operation;

import java.util.List;

/**
 * Cash flow control dao service
 */
@Repository
public class OperationsDAO {

    private static final Log logger = LogFactory.getLog(OperationsDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    public List<Operation> getOperations() throws CFException {
        try {
            final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Operation.class);
            criteria.addOrder(Order.desc("id"));
            //noinspection unchecked
            return criteria.list();
        } catch (HibernateException e) {
            logger.error("Error while collection operations", e);
            throw new CFException(e);
        }
    }


    public Operation insert(Operation model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            session.save(model);
            session.flush();

            return model;

        } catch (HibernateException e) {
            logger.error("Error while inserting new operation", e);
            throw new CFException(e);
        }
    }

    public Operation update(Operation model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            model = (Operation) session.merge(model);
            session.update(model);
            session.flush();

            return model;

        } catch (HibernateException e) {
            logger.error("Error while updating existing operation", e);
            throw new CFException(e);
        }
    }

    public Operation get(Integer id) throws CFException {
        try {
            return (Operation) sessionFactory.getCurrentSession().get(Operation.class, id);
        } catch (HibernateException e) {
            logger.error("Error while geting existing operation from database", e);
            throw new CFException(e);
        }
    }

    public void delete(Integer id) throws CFException {
        try {
            final Operation operation = get(id);
            final Session session = sessionFactory.getCurrentSession();
            session.delete(operation);
            session.flush();
        } catch (IllegalArgumentException | HibernateException e) {
            logger.error("Error while deleteing existing operation", e);
            throw new CFException(e);
        }
    }
}

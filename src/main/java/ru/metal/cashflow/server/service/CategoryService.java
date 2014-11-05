package ru.metal.cashflow.server.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Category;

/**
 * DAO service to work with category
 */
@Repository
public class CategoryService implements CRUDService<Category> {

    private static final Log logger = LogFactory.getLog(CategoryService.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void insert(Category model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            session.save(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while inserting new category", e);
            throw new CFException("Error while inserting new category", e);
        }
    }

    @Override
    public void update(Category model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            model = (Category) session.merge(model);
            session.update(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while updating existing category", e);
            throw new CFException("Error while updating existing category", e);
        }
    }

    @Override
    public Category get(Integer id) throws CFException {
        try {
            return (Category) sessionFactory.getCurrentSession().get(Category.class, id);
        } catch (HibernateException e) {
            logger.error("Error while geting existing category from database", e);
            throw new CFException("Error while geting existing category from database", e);
        }
    }

    @Override
    public void delete(Integer id) throws CFException {
        try {
            final Category currency = get(id);
            final Session session = sessionFactory.getCurrentSession();
            session.delete(currency);
            session.flush();
        } catch (IllegalArgumentException | HibernateException e) {
            logger.error("Error while deleteing existing category", e);
            throw new CFException("Error while deleteing existing category", e);
        }
    }
}

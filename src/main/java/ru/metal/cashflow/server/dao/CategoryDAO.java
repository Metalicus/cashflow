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
import ru.metal.cashflow.server.model.Category;

import java.util.List;

/**
 * DAO service to work with category
 */
@Repository
public class CategoryDAO {

    private static final Log logger = LogFactory.getLog(CategoryDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Get collection of the categories
     *
     * @param limit of the elements
     * @param page  page
     * @return collection of categories
     */
    public List<Category> getPagedCategory(int limit, int page) throws CFException {
        try {
            int start;
            if (page == 1) {
                // first page
                start = 0;
            } else {
                // next pages
                page = page - 1;
                start = page * limit;
            }

            final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Category.class);
            criteria.setFirstResult(start);
            criteria.setMaxResults(limit);

            //noinspection unchecked
            return criteria.list();
        } catch (HibernateException e) {
            logger.error("Error while collection categories", e);
            throw new CFException(e);
        }
    }

    public void insert(Category model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            session.save(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while inserting new category", e);
            throw new CFException(e);
        }
    }

    public void update(Category model) throws CFException {
        try {
            final Session session = sessionFactory.getCurrentSession();
            model = (Category) session.merge(model);
            session.update(model);
            session.flush();
        } catch (HibernateException e) {
            logger.error("Error while updating existing category", e);
            throw new CFException(e);
        }
    }

    public Category get(Integer id) throws CFException {
        try {
            return (Category) sessionFactory.getCurrentSession().get(Category.class, id);
        } catch (HibernateException e) {
            logger.error("Error while geting existing category from database", e);
            throw new CFException(e);
        }
    }

    public void delete(Integer id) throws CFException {
        try {
            final Category category = get(id);
            final Session session = sessionFactory.getCurrentSession();
            session.delete(category);
            session.flush();
        } catch (IllegalArgumentException | HibernateException e) {
            logger.error("Error while deleteing existing category", e);
            throw new CFException(e);
        }
    }
}

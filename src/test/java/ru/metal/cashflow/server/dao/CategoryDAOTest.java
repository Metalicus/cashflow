package ru.metal.cashflow.server.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Category;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import static org.junit.Assert.*;

public class CategoryDAOTest extends SpringTestCase {

    @Autowired
    private CategoryDAO categoryDAO;

    @Test
    public void saveTest() throws Exception {
        final Category category = new Category();
        category.setName("new category");
        assertNull(category.getId());

        categoryDAO.insert(category);
        assertNotNull(category.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Category categoryFromDB = categoryDAO.get(category.getId());
        assertEquals(category, categoryFromDB);
    }

    @Test(expected = CFException.class)
    public void saveErrorTest() throws Exception {
        final Category account = new Category();
        categoryDAO.insert(account);
    }

    @Test
    public void updateTest() throws Exception {
        final Category category = new Category();
        category.setName("category");
        assertNull(category.getId());

        categoryDAO.insert(category);
        final Integer id = category.getId();
        assertNotNull(id);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        category.setName("new category name");
        categoryDAO.update(category);
        // id doesnt change, it's the same object
        assertEquals(id, category.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        final Category categoryFromDB = categoryDAO.get(category.getId());
        assertEquals(category, categoryFromDB);
    }

    @Test(expected = CFException.class)
    public void updateErrorTest() throws Exception {
        final Category category = new Category();
        category.setName("category");
        categoryDAO.insert(category);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        category.setName(null);
        categoryDAO.update(category);
    }

    @Test
    public void getTest() throws Exception {
        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Category categoryFromDB = categoryDAO.get(category.getId());
        assertEquals(category, categoryFromDB);
    }

    @Test
    public void getNullTest() throws Exception {
        assertNull(categoryDAO.get(Integer.MAX_VALUE));
    }

    @Test
    public void deleteTest() throws Exception {
        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        categoryDAO.delete(category.getId());
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));
    }

    @Test(expected = CFException.class)
    public void deleteErrorTest() throws Exception {
        categoryDAO.delete(Integer.MAX_VALUE);
    }
}
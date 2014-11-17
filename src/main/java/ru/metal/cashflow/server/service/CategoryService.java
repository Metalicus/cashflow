package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.dao.CategoryDAO;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Category;

import java.util.List;

/**
 * Business layer for categories
 */
@Service
public class CategoryService implements CRUDService<Category> {

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public List<Category> list() throws CFException {
        return categoryDAO.getCategories();
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void insert(Category model) throws CFException {
        categoryDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Category model) throws CFException {
        categoryDAO.update(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Category get(int id) throws CFException {
        return categoryDAO.get(id);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        categoryDAO.delete(id);
    }
}

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

    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public List<Category> getCategories(int limit, int page) throws CFException {
        return categoryDAO.getPagedCategory(limit, page);
    }

    @Override
    public void insert(Category model) throws CFException {
        categoryDAO.insert(model);
    }

    @Override
    public void update(Category model) throws CFException {
        categoryDAO.update(model);
    }

    @Override
    public Category get(Integer id) throws CFException {
        return categoryDAO.get(id);
    }

    @Override
    public void delete(Integer id) throws CFException {
        categoryDAO.delete(id);
    }
}

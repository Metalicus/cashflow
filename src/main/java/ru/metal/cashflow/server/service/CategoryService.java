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
public class CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;

    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public List<Category> getCategories(int limit, int page) throws CFException {
        return categoryDAO.getPagedCategory(limit, page);
    }

}

package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.model.business.Category;
import ru.metal.cashflow.server.repository.CategoryRepository;
import ru.metal.cashflow.server.request.FilterRequest;

/**
 * Business layer for categories
 */
@Service("categoryService")
public class CategoryService implements CRUDService<Category> {

    @Autowired
    CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<Category> list(Pageable pageable, FilterRequest filterRequest) {
        return repository.findAll(pageable);
    }

    @Transactional
    public Category insert(Category model) {
        return repository.saveAndFlush(model);
    }

    @Transactional
    public Category update(Category model) {
        return repository.saveAndFlush(model);
    }

    @Transactional(readOnly = true)
    public Category get(int id) {
        return repository.findOne(id);
    }

    @Transactional
    public void delete(Integer id) {
        repository.delete(id);
    }
}

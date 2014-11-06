package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Category;
import ru.metal.cashflow.server.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(value = "category", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "table", method = RequestMethod.GET)
    public List<Category> getCategories(@RequestParam(value = "limit") int limit, @RequestParam(value = "page") int page) throws CFException {
        return categoryService.getCategories(limit, page);
    }
}

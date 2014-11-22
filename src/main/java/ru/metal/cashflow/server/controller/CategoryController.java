package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Category;
import ru.metal.cashflow.server.service.CategoryService;

@RestController
@RequestMapping(value = "category", produces = MainController.MEDIA_TYPE)
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Category save(@RequestBody Category category) throws CFException {
        if (category.getId() == null)
            return categoryService.insert(category);
        else
            return categoryService.update(category);
    }
}

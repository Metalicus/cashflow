package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.service.CategoryService;

@RestController
@RequestMapping(value = "category", produces = MainController.MEDIA_TYPE)
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
}

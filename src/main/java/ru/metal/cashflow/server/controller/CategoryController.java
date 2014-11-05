package ru.metal.cashflow.server.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.model.Category;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "category", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
public class CategoryController {

    @RequestMapping(value = "table", method = RequestMethod.GET)
    public List<Category> getCategories() {
        final List<Category> categories = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final Category category1 = new Category();
            category1.setName("category " + i);
            categories.add(category1);
        }

        return categories;
    }
}

package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.model.business.Category;
import ru.metal.cashflow.server.service.CRUDService;

@RestController
@RequestMapping(value = "category", produces = RestCRUDController.MEDIA_TYPE)
public class CategoryController extends RestCRUDController<Category> {

    @Autowired
    public CategoryController(CRUDService<Category> service) {
        super(service);
    }

}

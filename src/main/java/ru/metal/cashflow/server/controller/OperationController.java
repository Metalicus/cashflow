package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.model.Operation;
import ru.metal.cashflow.server.service.OperationService;

@RestController
@RequestMapping(value = "operation", produces = RestCRUDController.MEDIA_TYPE)
public class OperationController extends RestCRUDController<Operation> {

    @Autowired
    public OperationController(OperationService service) {
        super(service);
    }
}

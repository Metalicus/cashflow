package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Operation;
import ru.metal.cashflow.server.service.OperationService;

@RestController
@RequestMapping(value = "operation", produces = MainController.MEDIA_TYPE)
public class OperationController {

    @Autowired
    private OperationService operationService;

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Operation save(@RequestBody Operation operation) throws CFException {
        if (operation.getId() == null)
            return operationService.insert(operation);
        else
            return operationService.update(operation);
    }
}

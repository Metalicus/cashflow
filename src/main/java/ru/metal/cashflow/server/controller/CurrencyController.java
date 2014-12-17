package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.model.business.Currency;
import ru.metal.cashflow.server.service.CurrencyService;

@RestController
@RequestMapping(value = "currency", produces = RestCRUDController.MEDIA_TYPE)
public class CurrencyController extends RestCRUDController<Currency> {

    @Autowired
    public CurrencyController(CurrencyService service) {
        super(service);
    }

}

package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.service.CurrencyService;

@RestController
@RequestMapping(value = "currency", produces = MainController.MEDIA_TYPE)
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Currency save(@RequestBody Currency currency) throws CFException {
        if (currency.getId() == null)
            return currencyService.insert(currency);
        else
            return currencyService.update(currency);
    }
}

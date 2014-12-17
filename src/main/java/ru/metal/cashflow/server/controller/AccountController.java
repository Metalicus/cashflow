package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.model.business.Account;
import ru.metal.cashflow.server.service.AccountService;

@RestController
@RequestMapping(value = "account", produces = RestCRUDController.MEDIA_TYPE)
public class AccountController extends RestCRUDController<Account> {

    @Autowired
    public AccountController(AccountService service) {
        super(service);
    }

}

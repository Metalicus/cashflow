package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.service.AccountService;

@RestController
@RequestMapping(value = "account", produces = MainController.MEDIA_TYPE)
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Account save(@RequestBody Account account) throws CFException {
        if (account.getId() == null)
            return accountService.insert(account);
        else
            return accountService.update(account);
    }
}

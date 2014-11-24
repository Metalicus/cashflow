package ru.metal.cashflow.server.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.Currency;

import java.math.BigDecimal;

public class AccountServiceTest extends SpringTestCase {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CurrencyService currencyService;

    @Test(expected = CFException.class)
    public void insertInsertedTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("test currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("test account");
        account.setCurrency(currency);
        accountService.insert(account);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        accountService.insert(account);
    }

    @Test(expected = CFException.class)
    public void currencyChangeTest() throws Exception {
        final Currency currency1 = new Currency();
        currency1.setName("test currency");
        currencyService.insert(currency1);

        final Currency currency2 = new Currency();
        currency2.setName("test currency 2");
        currencyService.insert(currency2);

        final Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("test account");
        account.setCurrency(currency1);
        accountService.insert(account);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        account.setCurrency(currency2);
        accountService.update(account);
    }
}

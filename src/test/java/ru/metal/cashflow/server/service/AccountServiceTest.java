package ru.metal.cashflow.server.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountServiceTest extends SpringTestCase {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CurrencyService currencyService;

    @Test
    public void saveTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        assertNull(account.getId());

        accountService.insert(account);
        assertNotNull(account.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Account accountFromDB = accountService.get(account.getId());
        assertEquals(account, accountFromDB);
    }

    @Test(expected = CFException.class)
    public void saveErrorTest() throws Exception {
        final Account account = new Account();
        accountService.insert(account);
    }

    @Test
    public void updateTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountService.insert(account);

        final Integer id = account.getId();
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        account.setName("new account name");
        accountService.update(account);
        // id doesnt change, it's the same object
        assertEquals(id, account.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        final Account accountFromDB = accountService.get(account.getId());
        assertEquals(account, accountFromDB);
    }

    @Test(expected = CFException.class)
    public void updateErrorTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountService.insert(account);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        account.setCurrency(null);
        accountService.update(account);
    }

    @Test
    public void getTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountService.insert(account);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Account accountFromDB = accountService.get(account.getId());
        assertEquals(account, accountFromDB);
    }

    @Test
    public void getNullTest() throws Exception {
        assertNull(accountService.get(Integer.MAX_VALUE));
    }

    @Test
    public void deleteTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountService.insert(account);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        accountService.delete(account.getId());
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));
    }

    @Test(expected = CFException.class)
    public void deleteErrorTest() throws Exception {
        accountService.delete(Integer.MAX_VALUE);
    }
}

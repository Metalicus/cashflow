package ru.metal.cashflow.server.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountDAOTest extends SpringTestCase {

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private CurrencyDAO currencyDAO;

    @Test
    public void saveTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        assertNull(account.getId());

        accountDAO.insert(account);
        assertNotNull(account.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Account accountFromDB = accountDAO.get(account.getId());
        assertEquals(account, accountFromDB);
    }

    @Test(expected = CFException.class)
    public void saveErrorTest() throws Exception {
        final Account account = new Account();
        accountDAO.insert(account);
    }

    @Test
    public void updateTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Integer id = account.getId();
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        account.setName("new account name");
        accountDAO.update(account);
        // id doesnt change, it's the same object
        assertEquals(id, account.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        final Account accountFromDB = accountDAO.get(account.getId());
        assertEquals(account, accountFromDB);
    }

    @Test(expected = CFException.class)
    public void updateErrorTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        account.setCurrency(null);
        accountDAO.update(account);
    }

    @Test
    public void getTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Account accountFromDB = accountDAO.get(account.getId());
        assertEquals(account, accountFromDB);
    }

    @Test
    public void getNullTest() throws Exception {
        assertNull(accountDAO.get(Integer.MAX_VALUE));
    }

    @Test
    public void deleteTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        accountDAO.delete(account.getId());
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));
    }

    @Test(expected = CFException.class)
    public void deleteErrorTest() throws Exception {
        accountDAO.delete(Integer.MAX_VALUE);
    }
}

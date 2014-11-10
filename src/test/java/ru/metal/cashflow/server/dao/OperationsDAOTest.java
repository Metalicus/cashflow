package ru.metal.cashflow.server.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.*;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;

public class OperationsDAOTest extends SpringTestCase {

    @Autowired
    private OperationsDAO operationsDAO;
    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private CategoryDAO categoryDAO;

    @Test
    public void saveIncomeTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setInfo("information");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.INCOME);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        assertNull(operation.getId());
        operationsDAO.insert(operation);
        assertNotNull(operation.getId());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationsDAO.get(operation.getId());
        assertEquals(operation, operationFromDB);
    }

    @Test
    public void saveOutcomeTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setInfo("information");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.OUTCOME);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        assertNull(operation.getId());
        operationsDAO.insert(operation);
        assertNotNull(operation.getId());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationsDAO.get(operation.getId());
        assertEquals(operation, operationFromDB);
    }

    @Test
    public void saveTransferTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        final CrossCurrency crossCurrency = new CrossCurrency();
        crossCurrency.setAmount(BigDecimal.ONE);
        crossCurrency.setExchangeRate(BigDecimal.TEN);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setCrossCurrency(crossCurrency);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setInfo("information");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.TRANSFER);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        assertNull(operation.getId());
        operationsDAO.insert(operation);
        assertNotNull(operation.getId());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationsDAO.get(operation.getId());
        assertEquals(operation, operationFromDB);
    }

    @Test(expected = CFException.class)
    public void saveErrorTest() throws Exception {
        final Operation operation = new Operation();
        operationsDAO.insert(operation);
    }

    @Test
    public void updateTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        final CrossCurrency crossCurrency = new CrossCurrency();
        crossCurrency.setAmount(BigDecimal.ONE);
        crossCurrency.setExchangeRate(BigDecimal.TEN);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setCrossCurrency(crossCurrency);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setInfo("information");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.TRANSFER);
        operationsDAO.insert(operation);

        final Integer id = operation.getId();
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operation.setAmount(BigDecimal.valueOf(11));
        operationsDAO.update(operation);
        // id doesnt change, it's the same object
        assertEquals(id, operation.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        final Operation operationFromDB = operationsDAO.get(operation.getId());
        assertEquals(operation, operationFromDB);
    }

    @Test(expected = CFException.class)
    public void updateErrorTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setType(Operation.FlowType.TRANSFER);
        operationsDAO.insert(operation);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operation.setCurrency(null);
        operationsDAO.update(operation);
    }

    @Test
    public void getTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setType(Operation.FlowType.TRANSFER);
        operationsDAO.insert(operation);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationsDAO.get(operation.getId());
        assertEquals(operation, operationFromDB);
    }

    @Test
    public void getNullTest() throws Exception {
        assertNull(operationsDAO.get(Integer.MAX_VALUE));
    }

    @Test
    public void deleteTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyDAO.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountDAO.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryDAO.insert(category);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setType(Operation.FlowType.TRANSFER);
        operationsDAO.insert(operation);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operationsDAO.delete(operation.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
    }

    @Test(expected = CFException.class)
    public void deleteErrorTest() throws Exception {
        operationsDAO.delete(Integer.MAX_VALUE);
    }
}

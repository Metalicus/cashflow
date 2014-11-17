package ru.metal.cashflow.server.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.model.*;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;

public class OperationServiceTest extends SpringTestCase {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OperationService operationService;

    @Test
    public void insertTest1() throws Exception {
        // same currencies, simple insert
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("Test Account");
        account.setBalance(BigDecimal.TEN);
        account.setCurrency(currency);
        accountService.insert(account);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation = new Operation();
        operation.setAccount(account);
        operation.setCurrency(currency);
        operation.setCategory(category);
        operation.setDate(new Date());
        operation.setType(Operation.FlowType.INCOME);
        operation.setInfo("test info");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setAmount(BigDecimal.TEN);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        operationService.insert(operation);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operation.getId());
        assertEquals(operation, operationFromDB);
        assertNull(operationFromDB.getCrossCurrency());
    }

    @Test
    public void insertTest2() throws Exception {
        // cross currency operation
        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Currency currencyUSD = new Currency();
        currencyUSD.setName("USD");
        currencyService.insert(currencyUSD);

        final Account accountEUR = new Account();
        accountEUR.setName("Test Account");
        accountEUR.setBalance(BigDecimal.TEN);
        accountEUR.setCurrency(currencyEUR);
        accountService.insert(accountEUR);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operationUSD = new Operation();
        operationUSD.setAccount(accountEUR);
        operationUSD.setCurrency(currencyUSD);
        operationUSD.setCategory(category);
        operationUSD.setDate(new Date());
        operationUSD.setType(Operation.FlowType.OUTCOME);
        operationUSD.setInfo("test info");
        operationUSD.setMoneyWas(new BigDecimal("120.35"));
        operationUSD.setMoneyBecome(new BigDecimal("109.00"));
        operationUSD.setAmount(new BigDecimal("14.19"));

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        operationService.insert(operationUSD);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operationUSD.getId());
        assertEquals(operationUSD, operationFromDB);
        final CrossCurrency crossCurrency = operationFromDB.getCrossCurrency();
        assertNotNull(crossCurrency);
        assertEquals(new BigDecimal("11.35"), crossCurrency.getAmount());
        assertEquals(new BigDecimal("0.80"), crossCurrency.getExchangeRate());
    }
}

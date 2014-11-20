package ru.metal.cashflow.server.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.*;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    public void listTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountService.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setCurrency(currency);
        operation1.setAccount(account);
        operation1.setCategory(category);
        operation1.setAmount(BigDecimal.TEN);
        operation1.setDate(new Date());
        operation1.setInfo("information");
        operation1.setMoneyWas(BigDecimal.ZERO);
        operation1.setMoneyBecome(BigDecimal.TEN);
        operation1.setType(Operation.FlowType.EXPENSE);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setCurrency(currency);
        operation2.setAccount(account);
        operation2.setCategory(category);
        operation2.setAmount(BigDecimal.TEN);
        operation2.setDate(new Date());
        operation2.setInfo("information");
        operation2.setMoneyWas(BigDecimal.ZERO);
        operation2.setMoneyBecome(BigDecimal.TEN);
        operation2.setType(Operation.FlowType.EXPENSE);
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setCurrency(currency);
        operation3.setAccount(account);
        operation3.setCategory(category);
        operation3.setAmount(BigDecimal.TEN);
        operation3.setDate(new Date());
        operation3.setInfo("information");
        operation3.setMoneyWas(BigDecimal.ZERO);
        operation3.setMoneyBecome(BigDecimal.TEN);
        operation3.setType(Operation.FlowType.EXPENSE);
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final List<Operation> operations = operationService.list();
        assertNotNull(operations);
        assertEquals(3, operations.size());
        assertEquals(operation3, operations.get(0));
        assertEquals(operation2, operations.get(1));
        assertEquals(operation1, operations.get(2));
    }

    @Test
    public void getTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountService.insert(account);

        final Category category = new Category();
        category.setName("new category");
        categoryService.insert(category);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setDate(new Date());
        operation.setInfo("information");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.EXPENSE);
        operationService.insert(operation);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(operation, operationService.get(operation.getId()));
    }

    @Test
    public void deleteTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyService.insert(currency);

        final Currency currency2 = new Currency();
        currency2.setName("new currency");
        currencyService.insert(currency2);

        final Account account = new Account();
        account.setName("new account");
        account.setBalance(BigDecimal.valueOf(12));
        account.setCurrency(currency);
        accountService.insert(account);

        final Account account2 = new Account();
        account2.setName("new account");
        account2.setBalance(BigDecimal.valueOf(12));
        account2.setCurrency(currency);
        accountService.insert(account2);

        final Category category = new Category();
        category.setName("new category");
        categoryService.insert(category);

        final Transfer transfer = new Transfer();
        transfer.setTo(account2);

        final Operation operation = new Operation();
        operation.setAccount(account);
        operation.setCurrency(currency2);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setTransfer(transfer);
        operation.setDate(new Date());
        operation.setInfo("information");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.TRANSFER);
        operationService.insert(operation);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operationService.delete(operation.getId());

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));
    }

    @Test(expected = CFException.class)
    public void insertInsertedTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("Test Account");
        account.setBalance(BigDecimal.ONE);
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
        operationService.insert(operation);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operationService.insert(operation);
    }

    @Test(expected = CFException.class)
    public void changeTypeTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("Test Account");
        account.setBalance(BigDecimal.ONE);
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
        operationService.insert(operation);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operation.setType(Operation.FlowType.EXPENSE);
        operationService.update(operation);
    }

    @Test
    public void insertTest1() throws Exception {
        // same currencies, simple insert
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("Test Account");
        account.setBalance(BigDecimal.ONE);
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
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));
        operationService.insert(operation);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operation.getId());
        assertEquals(operation, operationFromDB);
        assertNull(operationFromDB.getCrossCurrency());

        assertEquals(new BigDecimal("10.00"), accountService.get(account.getId()).getBalance());
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
        operationUSD.setType(Operation.FlowType.EXPENSE);
        operationUSD.setInfo("test info");
        operationUSD.setMoneyWas(new BigDecimal("120.35"));
        operationUSD.setMoneyBecome(new BigDecimal("109.00"));
        operationUSD.setAmount(new BigDecimal("14.19"));

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));
        operationService.insert(operationUSD);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operationUSD.getId());
        assertEquals(operationUSD, operationFromDB);
        final CrossCurrency crossCurrency = operationFromDB.getCrossCurrency();
        assertNotNull(crossCurrency);
        assertEquals(new BigDecimal("11.35"), crossCurrency.getAmount());
        assertEquals(new BigDecimal("0.80"), crossCurrency.getExchangeRate());
    }

    @Test
    public void updateTest1() throws Exception {
        // operation was cross-currency, and it's still cross-currency, update it
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
        operationUSD.setType(Operation.FlowType.EXPENSE);
        operationUSD.setInfo("test info");
        operationUSD.setMoneyWas(new BigDecimal("120.35"));
        operationUSD.setMoneyBecome(new BigDecimal("109.00"));
        operationUSD.setAmount(new BigDecimal("14.19"));
        operationService.insert(operationUSD);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operationUSD.setMoneyBecome(new BigDecimal("107.14"));
        operationUSD.setAmount(new BigDecimal("14.00"));
        operationService.update(operationUSD);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operationUSD.getId());
        // update account
        accountEUR.setBalance(new BigDecimal("107.14"));
        assertEquals(operationUSD, operationFromDB);
        final CrossCurrency crossCurrency = operationFromDB.getCrossCurrency();
        assertNotNull(crossCurrency);
        assertEquals(new BigDecimal("13.21"), crossCurrency.getAmount());
        assertEquals(new BigDecimal("0.94"), crossCurrency.getExchangeRate());
    }

    @Test
    public void updateTest2() throws Exception {
        // operation was cross-currency, but now it's not. delete old cross-currency object
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
        operationUSD.setType(Operation.FlowType.EXPENSE);
        operationUSD.setInfo("test info");
        operationUSD.setMoneyWas(new BigDecimal("120.35"));
        operationUSD.setMoneyBecome(new BigDecimal("109.00"));
        operationUSD.setAmount(new BigDecimal("14.19"));
        operationService.insert(operationUSD);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        assertEquals(new BigDecimal("11.35"), operationUSD.getCrossCurrency().getAmount());

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operationUSD.setAmount(new BigDecimal("11.35"));
        operationUSD.setCurrency(accountEUR.getCurrency());
        operationService.update(operationUSD);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operationUSD.getId());
        assertEquals(operationUSD, operationFromDB);
        assertNull(operationFromDB.getCrossCurrency());
    }

    @Test
    public void updateTest3() throws Exception {
        // old operation was same currency operation, but operation became cross-currency
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

        final Operation operationEUR = new Operation();
        operationEUR.setAccount(accountEUR);
        operationEUR.setCurrency(currencyEUR);
        operationEUR.setCategory(category);
        operationEUR.setDate(new Date());
        operationEUR.setType(Operation.FlowType.EXPENSE);
        operationEUR.setInfo("test info");
        operationEUR.setMoneyWas(new BigDecimal("120.35"));
        operationEUR.setMoneyBecome(new BigDecimal("109.00"));
        operationEUR.setAmount(new BigDecimal("11.35"));
        operationService.insert(operationEUR);
        assertNull(operationEUR.getCrossCurrency());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operationEUR.setCurrency(currencyUSD);
        operationService.update(operationEUR);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operationEUR.getId());

        assertNotNull(operationEUR.getCrossCurrency());
        // to performe equals between object let's set id from created cross-currency
        operationEUR.getCrossCurrency().setId(operationFromDB.getCrossCurrency().getId());

        assertEquals(operationEUR, operationFromDB);
        final CrossCurrency crossCurrency = operationFromDB.getCrossCurrency();
        assertNotNull(crossCurrency);
        assertEquals(new BigDecimal("11.35"), crossCurrency.getAmount());
        assertEquals(new BigDecimal("1.00"), crossCurrency.getExchangeRate());
    }

    @Test
    public void updateTest4() throws Exception {
        // old operation was not cross-currency and updated still not cross-currency. just update
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account accountEUR = new Account();
        accountEUR.setName("Test Account");
        accountEUR.setBalance(BigDecimal.TEN);
        accountEUR.setCurrency(currency);
        accountService.insert(accountEUR);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation = new Operation();
        operation.setAccount(accountEUR);
        operation.setCurrency(currency);
        operation.setCategory(category);
        operation.setDate(new Date());
        operation.setType(Operation.FlowType.EXPENSE);
        operation.setInfo("test info");
        operation.setMoneyWas(new BigDecimal("120.35"));
        operation.setMoneyBecome(new BigDecimal("109.00"));
        operation.setAmount(new BigDecimal("11.35"));
        operationService.insert(operation);
        assertNull(operation.getCrossCurrency());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        operation.setInfo("test info");
        operationService.update(operation);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operation.getId());
        assertEquals(operation, operationFromDB);
    }

    @Test
    public void transferInsertTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account accountFrom = new Account();
        accountFrom.setName("Account FROM");
        accountFrom.setBalance(BigDecimal.ONE);
        accountFrom.setCurrency(currency);
        accountService.insert(accountFrom);

        final Account accountTo = new Account();
        accountTo.setName("Account To");
        accountTo.setBalance(BigDecimal.ONE);
        accountTo.setCurrency(currency);
        accountService.insert(accountTo);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Transfer transfer = new Transfer();
        transfer.setTo(accountTo);

        final Operation operation = new Operation();
        operation.setAccount(accountFrom);
        operation.setCurrency(currency);
        operation.setCategory(category);
        operation.setDate(new Date());
        operation.setType(Operation.FlowType.TRANSFER);
        operation.setInfo("test info");
        operation.setMoneyWas(BigDecimal.ZERO);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setAmount(BigDecimal.TEN);
        operation.setTransfer(transfer);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));
        operationService.insert(operation);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Operation operationFromDB = operationService.get(operation.getId());
        assertEquals(operation, operationFromDB);
        assertNull(operationFromDB.getCrossCurrency());

        assertEquals(new BigDecimal("10.00"), accountService.get(accountFrom.getId()).getBalance());
        assertEquals(new BigDecimal("11.00"), accountService.get(accountTo.getId()).getBalance());
    }

    @Test
    public void accountUpdateExpenseTest1() throws Exception {
        // let's test operation update and balance recalculation
        // expense, same account

        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("Account FROM");
        account.setBalance(new BigDecimal("100.00"));
        account.setCurrency(currency);
        accountService.insert(account);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        // 90
        final Operation operation1 = new Operation();
        operation1.setAccount(account);
        operation1.setCurrency(currency);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        // 73
        final Operation operation2 = new Operation();
        operation2.setAccount(account);
        operation2.setCurrency(currency);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.EXPENSE);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("73.00"));
        operation2.setAmount(new BigDecimal("17.00"));
        operationService.insert(operation2);

        // 24
        final Operation operation3 = new Operation();
        operation3.setAccount(account);
        operation3.setCurrency(currency);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("73.00"));
        operation3.setMoneyBecome(new BigDecimal("24.00"));
        operation3.setAmount(new BigDecimal("49.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        // update operation#2 to 33.
        operation2.setMoneyWas(new BigDecimal("90"));
        operation2.setMoneyBecome(new BigDecimal("57.00"));
        operation2.setAmount(new BigDecimal("33"));
        operationService.update(operation2);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("8.00"), accountService.get(account.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("73.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("24.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void accountUpdateExpenseTest2() throws Exception {
        // expense, different account

        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Currency currencyUSD = new Currency();
        currencyUSD.setName("USD");
        currencyService.insert(currencyUSD);

        final Account accountEUR = new Account();
        accountEUR.setName("Account EUR");
        accountEUR.setBalance(new BigDecimal("100.00"));
        accountEUR.setCurrency(currencyEUR);
        accountService.insert(accountEUR);

        final Account accountUSD = new Account();
        accountUSD.setName("Account USD");
        accountUSD.setBalance(new BigDecimal("100.00"));
        accountUSD.setCurrency(currencyUSD);
        accountService.insert(accountUSD);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setAccount(accountEUR);
        operation1.setCurrency(currencyEUR);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setAccount(accountUSD);
        operation2.setCurrency(currencyUSD);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.EXPENSE);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("100.00"));
        operation2.setMoneyBecome(new BigDecimal("83.00"));
        operation2.setAmount(new BigDecimal("17.00"));
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(accountEUR);
        operation3.setCurrency(currencyEUR);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("90.00"));
        operation3.setMoneyBecome(new BigDecimal("66.00"));
        operation3.setAmount(new BigDecimal("24.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("66.00"), accountService.get(accountEUR.getId()).getBalance());
        assertEquals(new BigDecimal("83.00"), accountService.get(accountUSD.getId()).getBalance());

        // update operation#2
        operation2.setAccount(accountEUR);
        operation2.setMoneyWas(new BigDecimal("90"));
        operation2.setMoneyBecome(new BigDecimal("57.00"));
        operation2.setAmount(new BigDecimal("33"));
        operationService.update(operation2);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("33.00"), accountService.get(accountEUR.getId()).getBalance());
        assertEquals(new BigDecimal("100.00"), accountService.get(accountUSD.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("90.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("66.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void accountUpdateExpenseTest3() throws Exception {
        // expense, cross currency

        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Currency currencyUSD = new Currency();
        currencyUSD.setName("USD");
        currencyService.insert(currencyUSD);

        final Account accountEUR = new Account();
        accountEUR.setName("Account EUR");
        accountEUR.setBalance(new BigDecimal("100.00"));
        accountEUR.setCurrency(currencyEUR);
        accountService.insert(accountEUR);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setAccount(accountEUR);
        operation1.setCurrency(currencyEUR);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setAccount(accountEUR);
        operation2.setCurrency(currencyUSD);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.EXPENSE);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("80.00"));
        operation2.setAmount(new BigDecimal("12.55")); // this is in the operation's currency, i.e. USD
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(accountEUR);
        operation3.setCurrency(currencyEUR);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("80.00"));
        operation3.setMoneyBecome(new BigDecimal("56.00"));
        operation3.setAmount(new BigDecimal("24.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("56.00"), accountService.get(accountEUR.getId()).getBalance());

        // update operation#2
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("70.00"));
        operation2.setAmount(new BigDecimal("25.09"));
        operationService.update(operation2);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("46.00"), accountService.get(accountEUR.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("80.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("56.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void accountUpdateIncomeTest1() throws Exception {
        // income, same account

        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("Account FROM");
        account.setBalance(new BigDecimal("100.00"));
        account.setCurrency(currency);
        accountService.insert(account);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setAccount(account);
        operation1.setCurrency(currency);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setAccount(account);
        operation2.setCurrency(currency);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.INCOME);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("107.00"));
        operation2.setAmount(new BigDecimal("17.00"));
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(account);
        operation3.setCurrency(currency);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("107.00"));
        operation3.setMoneyBecome(new BigDecimal("58.00"));
        operation3.setAmount(new BigDecimal("49.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        // update operation#2
        operation2.setMoneyWas(new BigDecimal("90"));
        operation2.setMoneyBecome(new BigDecimal("57.00"));
        operation2.setAmount(new BigDecimal("20.00"));
        operationService.update(operation2);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("61.00"), accountService.get(account.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("107.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("58.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void accountUpdateIncomeTest2() throws Exception {
        // income, different account

        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Currency currencyUSD = new Currency();
        currencyUSD.setName("USD");
        currencyService.insert(currencyUSD);

        final Account accountEUR = new Account();
        accountEUR.setName("Account EUR");
        accountEUR.setBalance(new BigDecimal("100.00"));
        accountEUR.setCurrency(currencyEUR);
        accountService.insert(accountEUR);

        final Account accountUSD = new Account();
        accountUSD.setName("Account USD");
        accountUSD.setBalance(new BigDecimal("100.00"));
        accountUSD.setCurrency(currencyUSD);
        accountService.insert(accountUSD);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setAccount(accountEUR);
        operation1.setCurrency(currencyEUR);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setAccount(accountUSD);
        operation2.setCurrency(currencyUSD);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.INCOME);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("100.00"));
        operation2.setMoneyBecome(new BigDecimal("117.00"));
        operation2.setAmount(new BigDecimal("17.00"));
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(accountEUR);
        operation3.setCurrency(currencyEUR);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("90.00"));
        operation3.setMoneyBecome(new BigDecimal("66.00"));
        operation3.setAmount(new BigDecimal("24.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("66.00"), accountService.get(accountEUR.getId()).getBalance());
        assertEquals(new BigDecimal("117.00"), accountService.get(accountUSD.getId()).getBalance());

        // update operation#2
        operation2.setAccount(accountEUR);
        operation2.setMoneyWas(new BigDecimal("90"));
        operation2.setMoneyBecome(new BigDecimal("123.00"));
        operation2.setAmount(new BigDecimal("33"));
        operationService.update(operation2);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("99.00"), accountService.get(accountEUR.getId()).getBalance());
        assertEquals(new BigDecimal("100.00"), accountService.get(accountUSD.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("90.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("66.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void accountUpdateIncomeTest3() throws Exception {
        // income, cross currency

        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Currency currencyUSD = new Currency();
        currencyUSD.setName("USD");
        currencyService.insert(currencyUSD);

        final Account accountEUR = new Account();
        accountEUR.setName("Account EUR");
        accountEUR.setBalance(new BigDecimal("100.00"));
        accountEUR.setCurrency(currencyEUR);
        accountService.insert(accountEUR);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setAccount(accountEUR);
        operation1.setCurrency(currencyEUR);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setAccount(accountEUR);
        operation2.setCurrency(currencyUSD);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.INCOME);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("100.00"));
        operation2.setAmount(new BigDecimal("12.55")); // this is in the operation's currency, i.e. USD
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(accountEUR);
        operation3.setCurrency(currencyEUR);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("100.00"));
        operation3.setMoneyBecome(new BigDecimal("76.00"));
        operation3.setAmount(new BigDecimal("24.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("76.00"), accountService.get(accountEUR.getId()).getBalance());

        // update operation#2
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("110.00"));
        operation2.setAmount(new BigDecimal("25.09"));
        operationService.update(operation2);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("86.00"), accountService.get(accountEUR.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("100.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("76.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void accountUpdateTransferTest1() throws Exception {
        // transfer, same currencies

        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Account accountEUR1 = new Account();
        accountEUR1.setName("Account EUR#1");
        accountEUR1.setBalance(new BigDecimal("100.00"));
        accountEUR1.setCurrency(currencyEUR);
        accountService.insert(accountEUR1);

        final Account accountEUR2 = new Account();
        accountEUR2.setName("Account EUR#2");
        accountEUR2.setBalance(new BigDecimal("100.00"));
        accountEUR2.setCurrency(currencyEUR);
        accountService.insert(accountEUR2);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setAccount(accountEUR1);
        operation1.setCurrency(currencyEUR);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        final Transfer transfer = new Transfer();
        transfer.setTo(accountEUR2);
        final Operation operation2 = new Operation();
        operation2.setAccount(accountEUR1);
        operation2.setCurrency(currencyEUR);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.TRANSFER);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("73.00"));
        operation2.setAmount(new BigDecimal("17.00"));
        operation2.setTransfer(transfer);
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(accountEUR1);
        operation3.setCurrency(currencyEUR);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("73.00"));
        operation3.setMoneyBecome(new BigDecimal("49.00"));
        operation3.setAmount(new BigDecimal("24.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("49.00"), accountService.get(accountEUR1.getId()).getBalance());
        assertEquals(new BigDecimal("117.00"), accountService.get(accountEUR2.getId()).getBalance());

        // update operation#2
        operation2.setMoneyWas(new BigDecimal("90"));
        operation2.setMoneyBecome(new BigDecimal("57.00"));
        operation2.setAmount(new BigDecimal("33.00"));
        operation2.getTransfer().setAmount(new BigDecimal("33.00"));
        operationService.update(operation2);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("33.00"), accountService.get(accountEUR1.getId()).getBalance());
        assertEquals(new BigDecimal("133.00"), accountService.get(accountEUR2.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("73.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("49.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void accountUpdateTransferTest2() throws Exception {
        // income, cross currency

        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Currency currencyUSD = new Currency();
        currencyUSD.setName("USD");
        currencyService.insert(currencyUSD);

        final Account accountEUR = new Account();
        accountEUR.setName("Account EUR");
        accountEUR.setBalance(new BigDecimal("100.00"));
        accountEUR.setCurrency(currencyEUR);
        accountService.insert(accountEUR);

        final Account accountUSD = new Account();
        accountUSD.setName("Account USD");
        accountUSD.setBalance(new BigDecimal("100.00"));
        accountUSD.setCurrency(currencyUSD);
        accountService.insert(accountUSD);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        final Operation operation1 = new Operation();
        operation1.setAccount(accountEUR);
        operation1.setCurrency(currencyEUR);
        operation1.setCategory(category);
        operation1.setDate(new Date());
        operation1.setType(Operation.FlowType.EXPENSE);
        operation1.setInfo("operation#1");
        operation1.setMoneyWas(new BigDecimal("100.00"));
        operation1.setMoneyBecome(new BigDecimal("90.00"));
        operation1.setAmount(BigDecimal.TEN);
        operationService.insert(operation1);

        // transfer 12.55 USD to EUR account
        final Transfer transfer = new Transfer();
        transfer.setTo(accountUSD);
        final Operation operation2 = new Operation();
        operation2.setAccount(accountEUR);
        operation2.setCurrency(currencyUSD);
        operation2.setCategory(category);
        operation2.setDate(new Date());
        operation2.setType(Operation.FlowType.TRANSFER);
        operation2.setInfo("operation#2");
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("80.00"));
        operation2.setAmount(new BigDecimal("12.55")); // this is in the operation's currency, i.e. USD
        operation2.setTransfer(transfer);
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(accountEUR);
        operation3.setCurrency(currencyEUR);
        operation3.setCategory(category);
        operation3.setDate(new Date());
        operation3.setType(Operation.FlowType.EXPENSE);
        operation3.setInfo("operation#3");
        operation3.setMoneyWas(new BigDecimal("80.00"));
        operation3.setMoneyBecome(new BigDecimal("24.00"));
        operation3.setAmount(new BigDecimal("56.00"));
        operationService.insert(operation3);

        assertEquals(3, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Operation.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), CrossCurrency.class));
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Transfer.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("24.00"), accountService.get(accountEUR.getId()).getBalance());
        assertEquals(new BigDecimal("112.55"), accountService.get(accountUSD.getId()).getBalance());

        // update operation#2
        operation2.setMoneyWas(new BigDecimal("90.00"));
        operation2.setMoneyBecome(new BigDecimal("70.00"));
        operation2.setAmount(new BigDecimal("25.09"));
        operation2.getTransfer().setAmount(new BigDecimal("25.09"));
        operationService.update(operation2);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("14.00"), accountService.get(accountEUR.getId()).getBalance());
        assertEquals(new BigDecimal("125.09"), accountService.get(accountUSD.getId()).getBalance());

        //this equals for future operations rsecalculation. it will fails
        assertEquals(new BigDecimal("80.00"), operationService.get(operation3.getId()).getMoneyWas());
        assertEquals(new BigDecimal("24.00"), operationService.get(operation3.getId()).getMoneyBecome());
    }

    @Test
    public void transferInsertTest2() throws Exception {
        final Currency currencyEUR = new Currency();
        currencyEUR.setName("EUR");
        currencyService.insert(currencyEUR);

        final Account accountEUR1 = new Account();
        accountEUR1.setName("Account EUR#1");
        accountEUR1.setBalance(new BigDecimal("100.00"));
        accountEUR1.setCurrency(currencyEUR);
        accountService.insert(accountEUR1);

        final Account accountEUR2 = new Account();
        accountEUR2.setName("Account EUR#2");
        accountEUR2.setBalance(new BigDecimal("100.00"));
        accountEUR2.setCurrency(currencyEUR);
        accountService.insert(accountEUR2);

        final Category category = new Category();
        category.setName("Test Category");
        categoryService.insert(category);

        // i sent 50 euro but comission take 3 euro to this transaction
        final Transfer transfer = new Transfer();
        transfer.setTo(accountEUR2);
        transfer.setAmount(new BigDecimal("50.00"));

        final Operation operation = new Operation();
        operation.setAccount(accountEUR1);
        operation.setCurrency(currencyEUR);
        operation.setCategory(category);
        operation.setDate(new Date());
        operation.setType(Operation.FlowType.TRANSFER);
        operation.setInfo("operation");
        operation.setMoneyWas(new BigDecimal("100.00"));
        operation.setMoneyBecome(new BigDecimal("47.00"));
        operation.setAmount(new BigDecimal("53.00"));
        operation.setTransfer(transfer);
        operationService.insert(operation);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("47.00"), accountService.get(accountEUR1.getId()).getBalance());
        assertEquals(new BigDecimal("150.00"), accountService.get(accountEUR2.getId()).getBalance());

        operation.setAmount(new BigDecimal("63.00"));
        operation.getTransfer().setAmount(new BigDecimal("60.00"));
        operationService.update(operation);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        assertEquals(new BigDecimal("37.00"), accountService.get(accountEUR1.getId()).getBalance());
        assertEquals(new BigDecimal("160.00"), accountService.get(accountEUR2.getId()).getBalance());

    }
}

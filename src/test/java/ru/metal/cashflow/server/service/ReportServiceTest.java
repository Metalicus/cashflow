package ru.metal.cashflow.server.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.model.business.*;
import ru.metal.cashflow.server.model.report.MonthlyBalance;
import ru.metal.cashflow.server.utils.DateUtils;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ReportServiceTest extends SpringTestCase {

    @Autowired
    AccountService accountService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    OperationService operationService;
    @Autowired
    ReportService reportService;

    @Test
    public void buildMonthlyReportTest() throws Exception {
        //----------------------- currencies

        final Currency eur = new Currency();
        eur.setName("EUR");
        currencyService.insert(eur);

        final Currency rub = new Currency();
        rub.setName("RUB");
        currencyService.insert(rub);

        //----------------------- accounts

        final Account cash = new Account();
        cash.setName("cash");
        cash.setBalance(new BigDecimal("43.75"));
        cash.setCurrency(eur);
        accountService.insert(cash);

        final Account bank1 = new Account();
        bank1.setName("bank#1");
        bank1.setBalance(new BigDecimal("54907.18"));
        bank1.setCurrency(rub);
        accountService.insert(bank1);

        final Account bank2 = new Account();
        bank2.setName("bank#2");
        bank2.setBalance(new BigDecimal("409.28"));
        bank2.setCurrency(rub);
        accountService.insert(bank2);


        //----------------------- categories

        final Category shop = new Category();
        shop.setName("shop");
        categoryService.insert(shop);

        final Category transfer = new Category();
        transfer.setName("transfer");
        categoryService.insert(transfer);

        final Category salary = new Category();
        salary.setName("salary");
        categoryService.insert(salary);

        final Category fastFood = new Category();
        fastFood.setName("fastfood");
        categoryService.insert(fastFood);

        final Category games = new Category();
        games.setName("games");
        categoryService.insert(games);

        //----------------------- operations

        final Operation o1 = new Operation();
        o1.setDate(DateUtils.create(2014, 6, 31));
        o1.setCurrency(eur);
        o1.setAccount(cash);
        o1.setCategory(shop);
        o1.setAmount(new BigDecimal("1.1"));
        o1.setMoneyWas(new BigDecimal("43.75"));
        o1.setMoneyBecome(new BigDecimal("42.65"));
        o1.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o1);

        final Transfer o2Transfer = new Transfer();
        o2Transfer.setAmount(new BigDecimal("40.00"));
        o2Transfer.setTo(cash);
        final Operation o2 = new Operation();
        o2.setDate(DateUtils.create(2014, 6, 31));
        o2.setCurrency(eur);
        o2.setAccount(bank1);
        o2.setCategory(transfer);
        o2.setAmount(new BigDecimal("40.00"));
        o2.setMoneyWas(new BigDecimal("54907.18"));
        o2.setMoneyBecome(new BigDecimal("52889.71"));
        o2.setType(Operation.FlowType.TRANSFER);
        o2.setTransfer(o2Transfer);
        operationService.insert(o2);

        final Operation o3 = new Operation();
        o3.setDate(DateUtils.create(2014, 7, 1));
        o3.setCurrency(eur);
        o3.setAccount(bank1);
        o3.setCategory(shop);
        o3.setAmount(new BigDecimal("29.44"));
        o3.setMoneyWas(new BigDecimal("52889.71"));
        o3.setMoneyBecome(new BigDecimal("51491.45"));
        o3.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o3);

        final Operation o4 = new Operation();
        o4.setDate(DateUtils.create(2014, 7, 5));
        o4.setCurrency(rub);
        o4.setAccount(bank2);
        o4.setCategory(salary);
        o4.setAmount(new BigDecimal("11686.58"));
        o4.setMoneyWas(new BigDecimal("409.28"));
        o4.setMoneyBecome(new BigDecimal("12095.86"));
        o4.setType(Operation.FlowType.INCOME);
        operationService.insert(o4);

        final Operation o5 = new Operation();
        o5.setDate(DateUtils.create(2014, 7, 9));
        o5.setCurrency(eur);
        o5.setAccount(cash);
        o5.setCategory(shop);
        o5.setAmount(new BigDecimal("15.30"));
        o5.setMoneyWas(new BigDecimal("100.00"));
        o5.setMoneyBecome(new BigDecimal("84.7"));
        o5.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o5);

        final Operation o6 = new Operation();
        o6.setDate(DateUtils.create(2014, 7, 11));
        o6.setCurrency(eur);
        o6.setAccount(cash);
        o6.setCategory(fastFood);
        o6.setAmount(new BigDecimal("3.00"));
        o6.setMoneyWas(new BigDecimal("80.8"));
        o6.setMoneyBecome(new BigDecimal("77.8"));
        o6.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o6);

        final Operation o7 = new Operation();
        o7.setDate(DateUtils.create(2014, 7, 15));
        o7.setCurrency(rub);
        o7.setAccount(bank1);
        o7.setCategory(salary);
        o7.setAmount(new BigDecimal("71720.00"));
        o7.setMoneyWas(new BigDecimal("15867.15"));
        o7.setMoneyBecome(new BigDecimal("87587.15"));
        o7.setType(Operation.FlowType.INCOME);
        operationService.insert(o7);

        final Operation o8 = new Operation();
        o8.setDate(DateUtils.create(2014, 7, 15));
        o8.setCurrency(eur);
        o8.setAccount(bank1);
        o8.setCategory(shop);
        o8.setAmount(new BigDecimal("29.08"));
        o8.setMoneyWas(new BigDecimal("17273.33"));
        o8.setMoneyBecome(new BigDecimal("15867.15"));
        o8.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o8);

        final Operation o9 = new Operation();
        o9.setDate(DateUtils.create(2014, 7, 15));
        o9.setCurrency(rub);
        o9.setAccount(bank2);
        o9.setCategory(games);
        o9.setAmount(new BigDecimal("300"));
        o9.setMoneyWas(new BigDecimal("779.93"));
        o9.setMoneyBecome(new BigDecimal("479.93"));
        o9.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o9);

        final Transfer o10Transfer = new Transfer();
        o10Transfer.setAmount(new BigDecimal("230.00"));
        o10Transfer.setTo(cash);
        final Operation o10 = new Operation();
        o10.setDate(DateUtils.create(2014, 7, 25));
        o10.setCurrency(eur);
        o10.setAccount(bank1);
        o10.setCategory(transfer);
        o10.setAmount(new BigDecimal("230.00"));
        o10.setMoneyWas(new BigDecimal("84545.8"));
        o10.setMoneyBecome(new BigDecimal("73423.53"));
        o10.setType(Operation.FlowType.TRANSFER);
        o10.setTransfer(o10Transfer);
        operationService.insert(o10);

        final Operation o11 = new Operation();
        o11.setDate(DateUtils.create(2014, 7, 27));
        o11.setCurrency(eur);
        o11.setAccount(cash);
        o11.setCategory(fastFood);
        o11.setAmount(new BigDecimal("22.9"));
        o11.setMoneyWas(new BigDecimal("105.45"));
        o11.setMoneyBecome(new BigDecimal("82.55"));
        o11.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o11);

        final Operation o12 = new Operation();
        o12.setDate(DateUtils.create(2014, 7, 29));
        o12.setCurrency(rub);
        o12.setAccount(bank2);
        o12.setCategory(shop);
        o12.setAmount(new BigDecimal("101.50"));
        o12.setMoneyWas(new BigDecimal("4225.62"));
        o12.setMoneyBecome(new BigDecimal("4124.12"));
        o12.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o12);

        final Operation o13 = new Operation();
        o13.setDate(DateUtils.create(2014, 8, 2));
        o13.setCurrency(eur);
        o13.setAccount(bank2);
        o13.setCategory(shop);
        o13.setAmount(new BigDecimal("49.55"));
        o13.setMoneyWas(new BigDecimal("4124.12"));
        o13.setMoneyBecome(new BigDecimal("1759"));
        o13.setType(Operation.FlowType.EXPENSE);
        operationService.insert(o13);

        // let's look what we have
        assertEquals(13, HibernateUtilsTest.executeCount(entityManager, Operation.class));
        assertEquals(2, HibernateUtilsTest.executeCount(entityManager, Transfer.class));
        assertEquals(5, HibernateUtilsTest.executeCount(entityManager, CrossCurrency.class));

        // let's create report
        final MonthlyBalance report = reportService.buildMonthlyReport(7, 2014);

        // expense
        final MonthlyBalance.Type expense = report.getExpense();
        assertEquals(3, expense.getRows().size());
        assertTrue(expense.getTotals().contains(new BigDecimal("3205.94"))); //2905.94 + 0 + 300
        assertTrue(expense.getTotals().contains(new BigDecimal("41.20"))); //15.30 + 25.90 + 0
        // 1

        MonthlyBalance.Row row = null;
        for (MonthlyBalance.Row iteratedRow : expense.getRows()) {
            if (iteratedRow.getCategory().equals(shop)) {
                row = iteratedRow;
                break;
            }
        }
        assertNotNull(row);
        assertEquals(2, row.getValues().size());
        assertTrue(row.getValues().contains(new BigDecimal("15.30")));
        assertTrue(row.getValues().contains(new BigDecimal("2905.94")));
        // 2
        row = null;
        for (MonthlyBalance.Row iteratedRow : expense.getRows()) {
            if (iteratedRow.getCategory().equals(fastFood)) {
                row = iteratedRow;
                break;
            }
        }
        assertNotNull(row);
        assertEquals(2, row.getValues().size());
        assertTrue(row.getValues().contains(new BigDecimal("25.90")));
        assertTrue(row.getValues().contains(BigDecimal.ZERO));
        // 3
        row = null;
        for (MonthlyBalance.Row iteratedRow : expense.getRows()) {
            if (iteratedRow.getCategory().equals(games)) {
                row = iteratedRow;
                break;
            }
        }
        assertNotNull(row);
        assertEquals(2, row.getValues().size());
        assertTrue(row.getValues().contains(BigDecimal.ZERO));
        assertTrue(row.getValues().contains(new BigDecimal("300.00")));

        // income
        final MonthlyBalance.Type income = report.getIncome();
        assertEquals(1, income.getRows().size());
        assertTrue(income.getTotals().contains(BigDecimal.ZERO));
        assertTrue(income.getTotals().contains(new BigDecimal("83406.58")));
        // 1
        row = income.getRows().get(0);
        assertEquals(salary, row.getCategory());
        assertEquals(2, row.getValues().size());
        assertTrue(row.getValues().contains(BigDecimal.ZERO));
        assertTrue(row.getValues().contains(new BigDecimal("83406.58")));

        // transfer
        final MonthlyBalance.Type transferFlow = report.getTransfer();
        assertEquals(1, transferFlow.getRows().size());
        assertTrue(transferFlow.getTotals().contains(BigDecimal.ZERO));
        assertTrue(transferFlow.getTotals().contains(new BigDecimal("11122.27")));
        // 1
        row = transferFlow.getRows().get(0);
        assertEquals(transfer, row.getCategory());
        assertEquals(2, row.getValues().size());
        assertTrue(row.getValues().contains(BigDecimal.ZERO));
        assertTrue(row.getValues().contains(new BigDecimal("11122.27")));
    }
}

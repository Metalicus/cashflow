package ru.metal.cashflow.server.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.Category;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.model.Operation;
import ru.metal.cashflow.server.service.AccountService;
import ru.metal.cashflow.server.service.CategoryService;
import ru.metal.cashflow.server.service.CurrencyService;
import ru.metal.cashflow.server.service.OperationService;
import ru.metal.cashflow.utils.HibernateUtilsTest;
import ru.metal.cashflow.utils.JSONUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OperationControllerTest extends SpringControllerTestCase {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OperationService operationService;

    @Test
    public void queryTest() throws Exception {
        final Category category = new Category();
        category.setName("EUR");
        categoryService.insert(category);

        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("test account");
        account.setCurrency(currency);
        accountService.insert(account);

        final Operation operation1 = new Operation();
        operation1.setDate(new Date());
        operation1.setCurrency(currency);
        operation1.setCategory(category);
        operation1.setAccount(account);
        operation1.setAmount(BigDecimal.TEN);
        operation1.setMoneyWas(BigDecimal.ONE);
        operation1.setMoneyBecome(BigDecimal.TEN);
        operation1.setType(Operation.FlowType.INCOME);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setDate(new Date());
        operation2.setCurrency(currency);
        operation2.setCategory(category);
        operation2.setAccount(account);
        operation2.setAmount(BigDecimal.TEN);
        operation2.setMoneyWas(BigDecimal.ONE);
        operation2.setMoneyBecome(BigDecimal.TEN);
        operation2.setType(Operation.FlowType.INCOME);
        operationService.insert(operation2);

        final MvcResult mvcResult = mockMvc.perform(get("/operation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RestCRUDController.MEDIA_TYPE))
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(2))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(operation1.getId()))
                .andExpect(jsonPath("$.content[1].id").value(operation2.getId()))
                .andReturn();

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(OperationController.class, handler.getBean().getClass());
        assertEquals("query", handler.getMethod().getName());
    }

    @Test
    public void getTest1() throws Exception {
        final Category category = new Category();
        category.setName("EUR");
        categoryService.insert(category);

        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("test account");
        account.setCurrency(currency);
        accountService.insert(account);

        final Operation operation = new Operation();
        operation.setDate(new Date());
        operation.setCurrency(currency);
        operation.setCategory(category);
        operation.setAccount(account);
        operation.setAmount(BigDecimal.TEN);
        operation.setMoneyWas(BigDecimal.ONE);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.INCOME);
        operationService.insert(operation);

        final MvcResult mvcResult = mockMvc.perform(get("/operation/" + operation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(operation, JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Operation.class));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(OperationController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }

    @Test
    public void insertTest() throws Exception {
        final Currency currency1 = new Currency();
        currency1.setName("EUR");
        currencyService.insert(currency1);

        final Currency currency2 = new Currency();
        currency2.setName("EUR");
        currencyService.insert(currency2);

        final Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("test account");
        account.setCurrency(currency1);
        accountService.insert(account);

        final Category category = new Category();
        category.setName("test category");
        categoryService.insert(category);

        final String json = "{" +
                "  \"id\": null," +
                "  \"type\": \"EXPENSE\"," +
                "  \"date\": \"2014-11-10T21:00:00.000Z\"," +
                "  \"amount\": 27.38," +
                "  \"moneyWas\": 15858.71," +
                "  \"moneyBecome\": 14351.56," +
                "  \"account\": {" +
                "    \"id\": " + account.getId() + "," +
                "    \"currency\": {" +
                "       \"id\": " + currency1.getId() +
                "    }" +
                "  }," +
                "  \"currency\": {" +
                "    \"id\": " + currency2.getId() +
                "  }," +
                "  \"category\": {" +
                "    \"id\": " + category.getId() +
                "  }," +
                "  \"info\": \"test info\"" +
                "}";

        assertEquals(0, HibernateUtilsTest.executeCount(entityManager, Operation.class));

        final MvcResult mvcResult = mockMvc.perform(post("/operation/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Operation responseOperation = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Operation.class);
        assertNotNull(responseOperation);

        final List<Operation> list = operationService.list(null, null).getContent();
        assertEquals(1, list.size());
        final Operation operation = list.get(0);
        assertEquals(new BigDecimal("27.38"), operation.getAmount());
        assertEquals(new BigDecimal("15858.71"), operation.getMoneyWas());
        assertEquals(new BigDecimal("14351.56"), operation.getMoneyBecome());
        assertEquals(Operation.FlowType.EXPENSE, operation.getType());
        assertEquals("test info", operation.getInfo());
        assertNotNull(operation.getCrossCurrency());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(OperationController.class, handler.getBean().getClass());
        assertEquals("create", handler.getMethod().getName());
    }

    @Test
    public void updateTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("test account");
        account.setCurrency(currency);
        accountService.insert(account);

        final Category category = new Category();
        category.setName("test category");
        categoryService.insert(category);

        final Operation operation = new Operation();
        operation.setCurrency(currency);
        operation.setAccount(account);
        operation.setCategory(category);
        operation.setAmount(BigDecimal.TEN);
        operation.setType(Operation.FlowType.EXPENSE);
        operation.setDate(new Date());
        operationService.insert(operation);

        List<Operation> list = operationService.list(null, null).getContent();
        assertEquals(1, list.size());
        Operation operationFromDB = list.get(0);
        assertEquals(BigDecimal.TEN, operationFromDB.getAmount());
        assertEquals(BigDecimal.ZERO, operationFromDB.getMoneyWas());
        assertEquals(BigDecimal.ZERO, operationFromDB.getMoneyBecome());
        assertEquals(Operation.FlowType.EXPENSE, operationFromDB.getType());
        assertNull(operationFromDB.getInfo());
        assertNull(operation.getCrossCurrency());

        final String json = "{" +
                "  \"id\": " + operation.getId() + "," +
                "  \"type\": \"EXPENSE\"," +
                "  \"date\": \"2014-11-10T21:00:00.000Z\"," +
                "  \"amount\": 27.38," +
                "  \"moneyWas\": 15858.71," +
                "  \"moneyBecome\": 14351.56," +
                "  \"account\": {" +
                "    \"id\": " + account.getId() + "," +
                "    \"currency\": {" +
                "       \"id\": " + currency.getId() +
                "    }" +
                "  }," +
                "  \"currency\": {" +
                "    \"id\": " + currency.getId() +
                "  }," +
                "  \"category\": {" +
                "    \"id\": " + category.getId() +
                "  }," +
                "  \"info\": \"test info\"" +
                "}";

        final MvcResult mvcResult = mockMvc.perform(put("/operation/" + operation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        list = operationService.list(null, null).getContent();
        assertEquals(1, list.size());
        operationFromDB = list.get(0);
        assertEquals(new BigDecimal("27.38"), operationFromDB.getAmount());
        assertEquals(new BigDecimal("15858.71"), operationFromDB.getMoneyWas());
        assertEquals(new BigDecimal("14351.56"), operationFromDB.getMoneyBecome());
        assertEquals(Operation.FlowType.EXPENSE, operationFromDB.getType());
        assertEquals("test info", operationFromDB.getInfo());
        assertNull(operation.getCrossCurrency());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(OperationController.class, handler.getBean().getClass());
        assertEquals("update", handler.getMethod().getName());
    }

    @Test
    public void deleteTest() throws Exception {
        final Category category = new Category();
        category.setName("EUR");
        categoryService.insert(category);

        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("test account");
        account.setCurrency(currency);
        accountService.insert(account);

        final Operation operation = new Operation();
        operation.setDate(new Date());
        operation.setCurrency(currency);
        operation.setCategory(category);
        operation.setAccount(account);
        operation.setAmount(BigDecimal.TEN);
        operation.setMoneyWas(BigDecimal.ONE);
        operation.setMoneyBecome(BigDecimal.TEN);
        operation.setType(Operation.FlowType.INCOME);
        operationService.insert(operation);

        final MvcResult mvcResult = mockMvc.perform(delete("/operation/" + operation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(OperationController.class, handler.getBean().getClass());
        assertEquals("delete", handler.getMethod().getName());
    }
}

package ru.metal.cashflow.server.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.service.AccountService;
import ru.metal.cashflow.server.service.CurrencyService;
import ru.metal.cashflow.utils.HibernateUtilsTest;
import ru.metal.cashflow.utils.JSONUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTest extends SpringControllerTestCase {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AccountService accountService;

    @Test
    public void queryTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("test currency");
        currencyService.insert(currency);

        final Account account1 = new Account();
        account1.setName("test account 1");
        account1.setBalance(new BigDecimal("99.99"));
        account1.setCurrency(currency);
        accountService.insert(account1);

        final Account account2 = new Account();
        account2.setName("test account 2");
        account2.setBalance(new BigDecimal("1.10"));
        account2.setCurrency(currency);
        accountService.insert(account2);

        final MvcResult mvcResult = mockMvc.perform(get("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final List<Account> accounts = Arrays.asList(JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Account[].class));
        assertEquals(2, accounts.size());

        assertEquals(account1, accounts.get(0));
        assertEquals(account2, accounts.get(1));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(AccountController.class, handler.getBean().getClass());
        assertEquals("query", handler.getMethod().getName());
    }

    @Test
    public void getTest1() throws Exception {
        final Currency currency = new Currency();
        currency.setName("test currency");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setName("test account");
        account.setBalance(new BigDecimal("99.99"));
        account.setCurrency(currency);
        accountService.insert(account);

        final MvcResult mvcResult = mockMvc.perform(get("/account/" + account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Account fromJSON = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Account.class);
        assertEquals(account, fromJSON);

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(AccountController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }

    @Test
    public void insertTest() throws Exception {
        final Currency category = new Currency();
        category.setName("test currency");
        currencyService.insert(category);

        final Account account = new Account();
        account.setName("test account");
        account.setBalance(new BigDecimal("99.99"));
        account.setCurrency(category);

        final String json = JSONUtils.toJSON(account);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        final MvcResult mvcResult = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Account responseAccount = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Account.class);
        assertNotNull(responseAccount);
        assertNotNull(responseAccount.getId());
        assertEquals(account.getName(), responseAccount.getName());
        assertEquals(account.getCurrency(), responseAccount.getCurrency());
        assertEquals(account.getBalance(), responseAccount.getBalance());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(AccountController.class, handler.getBean().getClass());
        assertEquals("create", handler.getMethod().getName());
    }

    @Test
    public void updateTest() throws Exception {
        final Currency category = new Currency();
        category.setName("test currency");
        currencyService.insert(category);

        final Account account = new Account();
        account.setName("test account");
        account.setBalance(new BigDecimal("99.99"));
        account.setCurrency(category);
        accountService.insert(account);

        final String json = JSONUtils.toJSON(account);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        final MvcResult mvcResult = mockMvc.perform(put("/account/" + account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Account responseAccount = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Account.class);
        assertNotNull(responseAccount);
        assertNotNull(responseAccount.getId());
        assertEquals(account.getName(), responseAccount.getName());
        assertEquals(account.getCurrency(), responseAccount.getCurrency());
        assertEquals(account.getBalance(), responseAccount.getBalance());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Account.class));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(AccountController.class, handler.getBean().getClass());
        assertEquals("update", handler.getMethod().getName());
    }

    @Test
    public void deleteTest() throws Exception {
        final Currency category = new Currency();
        category.setName("test currency");
        currencyService.insert(category);

        final Account account = new Account();
        account.setName("test account");
        account.setBalance(new BigDecimal("99.99"));
        account.setCurrency(category);
        accountService.insert(account);

        final MvcResult mvcResult = mockMvc.perform(delete("/account/" + account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(AccountController.class, handler.getBean().getClass());
        assertEquals("delete", handler.getMethod().getName());
    }
}

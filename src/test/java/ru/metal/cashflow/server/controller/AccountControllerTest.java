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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTest extends SpringControllerTestCase {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AccountService accountService;

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

        final MvcResult mvcResult = mockMvc.perform(post("/account/save")
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
        assertEquals("save", handler.getMethod().getName());
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

        final MvcResult mvcResult = mockMvc.perform(post("/account/save")
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
        assertEquals("save", handler.getMethod().getName());
    }
}

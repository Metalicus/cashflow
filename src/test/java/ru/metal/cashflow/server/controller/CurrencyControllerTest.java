package ru.metal.cashflow.server.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.service.CurrencyService;
import ru.metal.cashflow.utils.HibernateUtilsTest;
import ru.metal.cashflow.utils.JSONUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CurrencyControllerTest extends SpringControllerTestCase {

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void queryTest() throws Exception {
        final Currency currency1 = new Currency();
        currency1.setName("EUR");
        currencyService.insert(currency1);

        final Currency currency2 = new Currency();
        currency2.setName("USD");
        currencyService.insert(currency2);

        final MvcResult mvcResult = mockMvc.perform(get("/currency")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final List<Currency> currencies = Arrays.asList(JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Currency[].class));
        assertEquals(2, currencies.size());

        assertEquals(currency1.getId(), currencies.get(0).getId());
        assertEquals(currency1.getName(), currencies.get(0).getName());

        assertEquals(currency2.getId(), currencies.get(1).getId());
        assertEquals(currency2.getName(), currencies.get(1).getName());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CurrencyController.class, handler.getBean().getClass());
        assertEquals("query", handler.getMethod().getName());
    }

    @Test
    public void getTest1() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final MvcResult mvcResult = mockMvc.perform(get("/currency/" + currency.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Currency fromJSON = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Currency.class);
        assertEquals(currency.getId(), fromJSON.getId());
        assertEquals(currency.getName(), fromJSON.getName());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CurrencyController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }

    @Test
    public void insertTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("test currency");

        final String json = JSONUtils.toJSON(currency);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        final MvcResult mvcResult = mockMvc.perform(post("/currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Currency responseCurrency = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Currency.class);
        assertNotNull(responseCurrency);
        assertNotNull(responseCurrency.getId());
        assertEquals(currency.getName(), responseCurrency.getName());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CurrencyController.class, handler.getBean().getClass());
        assertEquals("create", handler.getMethod().getName());
    }

    @Test
    public void updateTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("test currency");
        currencyService.insert(currency);

        final String json = JSONUtils.toJSON(currency);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        final MvcResult mvcResult = mockMvc.perform(put("/currency/" + currency.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        final Currency responseCurrency = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Currency.class);
        assertNotNull(responseCurrency);
        assertEquals(currency, responseCurrency);

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CurrencyController.class, handler.getBean().getClass());
        assertEquals("update", handler.getMethod().getName());
    }

    @Test
    public void deleteTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final MvcResult mvcResult = mockMvc.perform(delete("/currency/" + currency.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CurrencyController.class, handler.getBean().getClass());
        assertEquals("delete", handler.getMethod().getName());
    }
}

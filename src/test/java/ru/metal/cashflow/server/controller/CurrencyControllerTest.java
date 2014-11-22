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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CurrencyControllerTest extends SpringControllerTestCase {

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void insertTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("test currency");

        final String json = JSONUtils.toJSON(currency);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        final MvcResult mvcResult = mockMvc.perform(post("/currency/save")
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
        assertEquals("save", handler.getMethod().getName());
    }

    @Test
    public void updateTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("test currency");
        currencyService.insert(currency);

        final String json = JSONUtils.toJSON(currency);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        final MvcResult mvcResult = mockMvc.perform(post("/currency/save")
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
        assertEquals("save", handler.getMethod().getName());
    }
}

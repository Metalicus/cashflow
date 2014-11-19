package ru.metal.cashflow.server.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.exception.JSONException;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.service.CurrencyService;
import ru.metal.cashflow.utils.JSONUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MainControllerTest extends SpringControllerTestCase {

    @Autowired
    private CurrencyService currencyService;

    // <editor-fold desc="list tests">

    @Test
    public void listTest1() throws Exception {
        final Currency currency1 = new Currency();
        currency1.setName("EUR");
        currencyService.insert(currency1);

        final Currency currency2 = new Currency();
        currency2.setName("USD");
        currencyService.insert(currency2);

        final MvcResult mvcResult = mockMvc.perform(get("/currency/list")
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
        assertEquals(MainController.class, handler.getBean().getClass());
        assertEquals("list", handler.getMethod().getName());
    }

    @Test
    public void listTest2() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/currency/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(0, JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Currency[].class).length);
    }

    @Test
    public void listTest3() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/abcd/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        final JSONException exception = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), JSONException.class);
        assertNotNull(exception);
        assertEquals("No bean named 'abcdService' is defined", exception.getMessage());
        assertFalse(exception.getStack().isEmpty());
    }

    // </editor-fold>

    // <editor-fold desc="get tests">

    @Test
    public void getTest1() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final MvcResult mvcResult = mockMvc.perform(get("/currency/get/" + currency.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Currency fromJSON = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Currency.class);
        assertEquals(currency.getId(), fromJSON.getId());
        assertEquals(currency.getName(), fromJSON.getName());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(MainController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }

    @Test
    public void getTest2() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/currency/get/" + Integer.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(MainController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }

    @Test
    public void getTest3() throws Exception {
        mockMvc.perform(get("/currency/get/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        final MvcResult mvcResult = mockMvc.perform(get("/currency/get/abcd")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        final JSONException exception = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), JSONException.class);
        assertNotNull(exception);
        assertEquals("Wrong request parameter", exception.getMessage());
        assertTrue(exception.getStack().isEmpty());
    }

    @Test
    public void getTest4() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/abcd/get/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        final JSONException exception = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), JSONException.class);
        assertNotNull(exception);
        assertEquals("No bean named 'abcdService' is defined", exception.getMessage());
        assertFalse(exception.getStack().isEmpty());
    }

    // </editor-fold>

    // <editor-fold desc="delete tests">

    @Test
    public void deleteTest1() throws Exception {
        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final MvcResult mvcResult = mockMvc.perform(get("/currency/delete/" + currency.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(MainController.class, handler.getBean().getClass());
        assertEquals("delete", handler.getMethod().getName());
    }

    @Test
    public void deleteTest2() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/currency/delete/" + Integer.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        final JSONException exception = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), JSONException.class);
        assertNotNull(exception);
        assertEquals("Currency is not found", exception.getMessage());
        assertFalse(exception.getStack().isEmpty());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(MainController.class, handler.getBean().getClass());
        assertEquals("delete", handler.getMethod().getName());
    }

    @Test
    public void deleteTest3() throws Exception {
        mockMvc.perform(get("/currency/delete/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        final MvcResult mvcResult = mockMvc.perform(get("/currency/delete/abcd")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        final JSONException exception = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), JSONException.class);
        assertNotNull(exception);
        assertEquals("Wrong request parameter", exception.getMessage());
        assertTrue(exception.getStack().isEmpty());
    }

    @Test
    public void deleteTest4() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/abcd/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        final JSONException exception = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), JSONException.class);
        assertNotNull(exception);
        assertEquals("No bean named 'abcdService' is defined", exception.getMessage());
        assertFalse(exception.getStack().isEmpty());
    }

    // </editor-fold>
}

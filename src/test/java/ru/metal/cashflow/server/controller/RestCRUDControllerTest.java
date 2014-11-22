package ru.metal.cashflow.server.controller;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.exception.JSONException;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.utils.JSONUtils;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Common tests for controllers
 */
public class RestCRUDControllerTest extends SpringControllerTestCase {

    @Test
    public void queryTest1() throws Exception {
        mockMvc.perform(get("/abcd")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void queryTest2() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(0, JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Currency[].class).length);
    }

    @Test
    public void getTest1() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/currency/" + Integer.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CurrencyController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }

    @Test
    public void getTest2() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/currency/abcd")
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
    public void deleteTest2() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(delete("/currency/" + Integer.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        final JSONException exception = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), JSONException.class);
        assertNotNull(exception);
        assertEquals("Currency is not found", exception.getMessage());
        assertFalse(exception.getStack().isEmpty());

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CurrencyController.class, handler.getBean().getClass());
        assertEquals("delete", handler.getMethod().getName());
    }

    @Test
    public void deleteTest3() throws Exception {
        mockMvc.perform(delete("/currency/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final MvcResult mvcResult = mockMvc.perform(delete("/currency/abcd")
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
        mockMvc.perform(delete("/abcd/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}

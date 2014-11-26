package ru.metal.cashflow.server.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.exception.JSONException;
import ru.metal.cashflow.server.model.Category;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.service.CategoryService;
import ru.metal.cashflow.utils.JSONUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Common tests for controllers
 */
public class RestCRUDControllerTest extends SpringControllerTestCase {

    @Autowired
    CategoryService categoryService;

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
        assertEquals("No class ru.metal.cashflow.server.model.Currency entity with id 2147483647 exists!", exception.getMessage());
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

    @Test
    public void paginationTest() throws Exception {
        final String format = "%1$02d";
        final List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            final Category category = new Category();
            category.setName("a" + String.format(format, i));
            categoryService.insert(category);

            categories.add(category);
        }

        MvcResult mvcResult = mockMvc.perform(get("/category?page=0&size=1&sort=id")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Category> fromServer = Arrays.asList(JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category[].class));
        assertEquals(1, fromServer.size());
        assertEquals(categories.get(0), fromServer.get(0));

        mvcResult = mockMvc.perform(get("/category?page=0&size=2&sort=id,desc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        fromServer = Arrays.asList(JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category[].class));
        assertEquals(2, fromServer.size());
        assertEquals(categories.get(29), fromServer.get(0));
        assertEquals(categories.get(28), fromServer.get(1));

        mvcResult = mockMvc.perform(get("/category?page=0&size=20&sort=name,desc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        fromServer = Arrays.asList(JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category[].class));
        assertEquals(20, fromServer.size());
        assertEquals(categories.get(29), fromServer.get(0));
        assertEquals(categories.get(28), fromServer.get(1));
        assertEquals(categories.get(27), fromServer.get(2));
    }

    @Test
    public void withoutPagintaionTest() throws Exception {
        final String format = "%1$02d";
        final List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            final Category category = new Category();
            category.setName("a" + String.format(format, i));
            categoryService.insert(category);

            categories.add(category);
        }

        // we don't want Pageable
        final MvcResult mvcResult = mockMvc.perform(get("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // returns collection without limits and without sorting
        final List<Category> fromServer = Arrays.asList(JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category[].class));
        assertEquals(30, fromServer.size());
        assertEquals(categories.get(0), fromServer.get(0));
        assertEquals(categories.get(9), fromServer.get(9));
        assertEquals(categories.get(29), fromServer.get(29));
    }
}

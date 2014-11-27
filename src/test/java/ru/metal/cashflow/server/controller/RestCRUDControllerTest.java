package ru.metal.cashflow.server.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.exception.JSONException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.Category;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.model.Operation;
import ru.metal.cashflow.server.service.AccountService;
import ru.metal.cashflow.server.service.CategoryService;
import ru.metal.cashflow.server.service.CurrencyService;
import ru.metal.cashflow.server.service.OperationService;
import ru.metal.cashflow.utils.JSONUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Common tests for controllers
 */
public class RestCRUDControllerTest extends SpringControllerTestCase {

    @Autowired
    CategoryService categoryService;
    @Autowired
    AccountService accountService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    OperationService operationService;

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

        assertEquals(0, JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Page.class).getContent().size());
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

        mockMvc.perform(get("/category?page=0&size=1&sort=id")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RestCRUDController.MEDIA_TYPE))
                .andExpect(jsonPath("totalPages").value(30))
                .andExpect(jsonPath("totalElements").value(30))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(categories.get(0).getId()));

        mockMvc.perform(get("/category?page=0&size=2&sort=id,desc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RestCRUDController.MEDIA_TYPE))
                .andExpect(jsonPath("totalPages").value(15))
                .andExpect(jsonPath("totalElements").value(30))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(categories.get(29).getId()))
                .andExpect(jsonPath("$.content[1].id").value(categories.get(28).getId()));

        mockMvc.perform(get("/category?page=0&size=20&sort=name,desc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RestCRUDController.MEDIA_TYPE))
                .andExpect(jsonPath("totalPages").value(2))
                .andExpect(jsonPath("totalElements").value(30))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(20)))
                .andExpect(jsonPath("$.content[0].id").value(categories.get(29).getId()))
                .andExpect(jsonPath("$.content[1].id").value(categories.get(28).getId()))
                .andExpect(jsonPath("$.content[2].id").value(categories.get(27).getId()));
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
        mockMvc.perform(get("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(30))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(30)))
                .andExpect(jsonPath("$.content[0].id").value(categories.get(0).getId()))
                .andExpect(jsonPath("$.content[9].id").value(categories.get(9).getId()))
                .andExpect(jsonPath("$.content[29].id").value(categories.get(29).getId()));
    }

    @Test
    public void filteringTest() throws Exception {
        // prepare operation
        final Category category1 = new Category();
        category1.setName("category 1");
        categoryService.insert(category1);

        final Category category2 = new Category();
        category2.setName("category 2");
        categoryService.insert(category2);

        final Currency currency = new Currency();
        currency.setName("EUR");
        currencyService.insert(currency);

        final Account account = new Account();
        account.setCurrency(currency);
        account.setName("account");
        account.setBalance(BigDecimal.TEN);
        accountService.insert(account);

        final Operation operation1 = new Operation();
        operation1.setAccount(account);
        operation1.setCurrency(currency);
        operation1.setCategory(category1);
        operation1.setMoneyWas(BigDecimal.ZERO);
        operation1.setMoneyBecome(BigDecimal.TEN);
        operation1.setDate(new Date());
        operation1.setAmount(BigDecimal.TEN);
        operation1.setType(Operation.FlowType.INCOME);
        operationService.insert(operation1);

        final Operation operation2 = new Operation();
        operation2.setAccount(account);
        operation2.setCurrency(currency);
        operation2.setCategory(category2);
        operation2.setMoneyWas(BigDecimal.ZERO);
        operation2.setMoneyBecome(BigDecimal.TEN);
        operation2.setDate(new Date());
        operation2.setAmount(BigDecimal.TEN);
        operation2.setType(Operation.FlowType.INCOME);
        operationService.insert(operation2);

        final Operation operation3 = new Operation();
        operation3.setAccount(account);
        operation3.setCurrency(currency);
        operation3.setCategory(category2);
        operation3.setMoneyWas(BigDecimal.TEN);
        operation3.setMoneyBecome(BigDecimal.ZERO);
        operation3.setDate(new Date());
        operation3.setAmount(BigDecimal.TEN);
        operation3.setType(Operation.FlowType.EXPENSE);
        operationService.insert(operation3);

        mockMvc.perform(get("/operation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(3))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(3)));

        mockMvc.perform(get("/operation?category=" + category1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(operation1.getId()));

        mockMvc.perform(get("/operation?category=" + category2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(2))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(operation2.getId()))
                .andExpect(jsonPath("$.content[1].id").value(operation3.getId()));

        mockMvc.perform(get("/operation?page=0&size=1&sort=id,desc&category=" + category2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalPages").value(2))
                .andExpect(jsonPath("totalElements").value(2))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(operation3.getId()));
    }
}

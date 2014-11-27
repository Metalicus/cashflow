package ru.metal.cashflow.server.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;
import ru.metal.cashflow.server.model.Category;
import ru.metal.cashflow.server.service.CategoryService;
import ru.metal.cashflow.utils.HibernateUtilsTest;
import ru.metal.cashflow.utils.JSONUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryControllerTest extends SpringControllerTestCase {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void queryTest() throws Exception {
        final Category category1 = new Category();
        category1.setName("EUR");
        categoryService.insert(category1);

        final Category category2 = new Category();
        category2.setName("USD");
        categoryService.insert(category2);

        final MvcResult mvcResult = mockMvc.perform(get("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RestCRUDController.MEDIA_TYPE))
                .andExpect(jsonPath("totalPages").value(1))
                .andExpect(jsonPath("totalElements").value(2))
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(category1.getId()))
                .andExpect(jsonPath("$.content[1].id").value(category2.getId()))
                .andReturn();

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CategoryController.class, handler.getBean().getClass());
        assertEquals("query", handler.getMethod().getName());
    }

    @Test
    public void getTest1() throws Exception {
        final Category category = new Category();
        category.setName("EUR");
        categoryService.insert(category);

        final MvcResult mvcResult = mockMvc.perform(get("/category/" + category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(category, JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category.class));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CategoryController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }

    @Test
    public void insertTest() throws Exception {
        final Category category = new Category();
        category.setName("test category");

        final String json = JSONUtils.toJSON(category);

        assertEquals(0, HibernateUtilsTest.executeCount(entityManager, Category.class));

        final MvcResult mvcResult = mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Category responseCategory = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category.class);
        assertNotNull(responseCategory);
        assertNotNull(responseCategory.getId());
        assertEquals(category.getName(), responseCategory.getName());

        assertEquals(1, HibernateUtilsTest.executeCount(entityManager, Category.class));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CategoryController.class, handler.getBean().getClass());
        assertEquals("create", handler.getMethod().getName());
    }

    @Test
    public void updateTest() throws Exception {
        final Category category = new Category();
        category.setName("test category");
        categoryService.insert(category);

        final String json = JSONUtils.toJSON(category);

        assertEquals(1, HibernateUtilsTest.executeCount(entityManager, Category.class));

        final MvcResult mvcResult = mockMvc.perform(put("/category/" + category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(1, HibernateUtilsTest.executeCount(entityManager, Category.class));

        final Category responseCategory = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category.class);
        assertNotNull(responseCategory);
        assertEquals(category, responseCategory);

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CategoryController.class, handler.getBean().getClass());
        assertEquals("update", handler.getMethod().getName());
    }

    @Test
    public void deleteTest() throws Exception {
        final Category category = new Category();
        category.setName("test category");
        categoryService.insert(category);

        final MvcResult mvcResult = mockMvc.perform(delete("/category/" + category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CategoryController.class, handler.getBean().getClass());
        assertEquals("delete", handler.getMethod().getName());
    }
}

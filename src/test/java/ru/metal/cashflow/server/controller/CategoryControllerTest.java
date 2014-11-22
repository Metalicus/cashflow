package ru.metal.cashflow.server.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryControllerTest extends SpringControllerTestCase {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void insertTest() throws Exception {
        final Category category = new Category();
        category.setName("test category");

        final String json = JSONUtils.toJSON(category);

        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        final MvcResult mvcResult = mockMvc.perform(post("/category/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final Category responseCategory = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category.class);
        assertNotNull(responseCategory);
        assertNotNull(responseCategory.getId());
        assertEquals(category.getName(), responseCategory.getName());

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CategoryController.class, handler.getBean().getClass());
        assertEquals("save", handler.getMethod().getName());
    }

    @Test
    public void updateTest() throws Exception {
        final Category category = new Category();
        category.setName("test category");
        categoryService.insert(category);

        final String json = JSONUtils.toJSON(category);

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        final MvcResult mvcResult = mockMvc.perform(post("/category/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Category.class));

        final Category responseCategory = JSONUtils.fromJSON(mvcResult.getResponse().getContentAsString(), Category.class);
        assertNotNull(responseCategory);
        assertEquals(category, responseCategory);

        final HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(CategoryController.class, handler.getBean().getClass());
        assertEquals("save", handler.getMethod().getName());
    }
}
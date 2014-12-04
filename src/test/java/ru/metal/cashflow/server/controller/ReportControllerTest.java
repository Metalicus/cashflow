package ru.metal.cashflow.server.controller;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import ru.metal.cashflow.server.SpringControllerTestCase;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportControllerTest extends SpringControllerTestCase {

    @Test
    public void getReportTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/report/monthly_balance?month=1&year=214")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RestCRUDController.MEDIA_TYPE))
                .andReturn();

        assertEquals("{\"expense\":{\"currencies\":[],\"rows\":[]},\"income\":{\"currencies\":[],\"rows\":[]},\"transfer\":{\"currencies\":[],\"rows\":[]},\"reportType\":\"MONTHLY_BALANCE\"}", mvcResult.getResponse().getContentAsString());

        HandlerMethod handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(ReportController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());

        mvcResult = mockMvc.perform(get("/report/MONTHLY_BALANCE?month=1&year=214")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RestCRUDController.MEDIA_TYPE))
                .andReturn();

        assertEquals("{\"expense\":{\"currencies\":[],\"rows\":[]},\"income\":{\"currencies\":[],\"rows\":[]},\"transfer\":{\"currencies\":[],\"rows\":[]},\"reportType\":\"MONTHLY_BALANCE\"}", mvcResult.getResponse().getContentAsString());

        handler = (HandlerMethod) mvcResult.getHandler();
        assertEquals(ReportController.class, handler.getBean().getClass());
        assertEquals("get", handler.getMethod().getName());
    }
}

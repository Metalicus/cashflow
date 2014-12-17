package ru.metal.cashflow.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.metal.cashflow.server.resolver.FilteringHandlerMethodArgumentResolver;
import ru.metal.cashflow.server.resolver.ReportHandlerMethodArgumentResolver;
import ru.metal.cashflow.server.resolver.ReportTypeConverter;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan("ru.metal.cashflow.server.controller")
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        final PageableHandlerMethodArgumentResolver argumentResolver = new PageableHandlerMethodArgumentResolver();
        // we disabled  default Pageable so if you sending no pagable data findAll will return all objects in database
        // but now we can't send partial Pageable data, like only 'sort' or only 'size' without others parameters
        argumentResolver.setFallbackPageable(null);

        argumentResolvers.add(argumentResolver);
        argumentResolvers.add(new SortHandlerMethodArgumentResolver());
        argumentResolvers.add(new FilteringHandlerMethodArgumentResolver());
        argumentResolvers.add(new ReportHandlerMethodArgumentResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ReportTypeConverter());
    }
}

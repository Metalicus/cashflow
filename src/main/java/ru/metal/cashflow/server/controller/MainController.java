package ru.metal.cashflow.server.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.service.CRUDService;

@RestController
@RequestMapping(produces = MainController.MEDIA_TYPE)
public class MainController implements ApplicationContextAware {

    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8";

    private ApplicationContext applicationContext;

    @RequestMapping(value = "{beanName}/list", method = RequestMethod.GET)
    public Object list(@PathVariable("beanName") String beanName) throws CFException {
        final Object bean = applicationContext.getBean(getManagerName(beanName));
        if (bean instanceof CRUDService) {
            return ((CRUDService) bean).list();
        } else
            throw new CFException("Service is not has a CRUD support");
    }

    @RequestMapping(value = "{beanName}/get/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable("beanName") String beanName, @PathVariable("id") int id) throws CFException {
        final Object bean = applicationContext.getBean(getManagerName(beanName));
        if (bean instanceof CRUDService) {
            return ((CRUDService) bean).get(id);
        } else
            throw new CFException("Service is not has a CRUD support");
    }

    @RequestMapping(value = "{beanName}/delete/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("beanName") String beanName, @PathVariable int id) throws CFException {
        final Object bean = applicationContext.getBean(getManagerName(beanName));
        if (bean instanceof CRUDService) {
            ((CRUDService) bean).delete(id);
        } else
            throw new CFException("Service is not has a CRUD support");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String getManagerName(String beanName) {
        beanName += "Service";
        return beanName;
    }
}

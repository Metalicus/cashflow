package ru.metal.cashflow.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.service.CRUDService;

import java.util.List;

/**
 * Standart controller for CRUD operations
 * <p>
 * action/operation    - GET     - returns all operations<br/>
 * action/operation    - POST    - new operation<br/>
 * action/operation/id - GET     - return single operation<br/>
 * action/operation/id - PUT     - update operation<br/>
 * action/operation/id - DELETE  - delete operation<br/>
 *
 * @param <T> type of model
 */
public abstract class RestCRUDController<T> {

    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8";

    private CRUDService<T> service;

    public RestCRUDController(CRUDService<T> service) {
        this.service = service;
    }

    @RequestMapping
    public List<T> query() throws CFException {
        return service.list();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public T create(@RequestBody T model) throws CFException {
        return service.insert(model);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public T update(@PathVariable int id, @RequestBody T model) throws CFException {
        return service.update(model);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T get(@PathVariable int id) throws CFException {
        return service.get(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) throws CFException {
        service.delete(id);
    }
}

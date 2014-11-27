package ru.metal.cashflow.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.request.FilterRequest;

/**
 * Interface for Create/Read/Update/Delete services
 */
public interface CRUDService<T> {

    /**
     * Get list of all objects
     *
     * @param pageable      page restriction, can be {@code null}
     * @param filterRequest filtreing information, can be {@code null}
     * @return page with data
     * @throws CFException error while executing DB access
     */
    Page<T> list(Pageable pageable, FilterRequest filterRequest) throws CFException;

    /**
     * Insert new model to database
     *
     * @param model new model
     * @return newly created model
     * @throws CFException error while executing DB access
     */
    T insert(T model) throws CFException;

    /**
     * Update exisiting model
     *
     * @param model existing model
     * @return updated model
     * @throws CFException error while executing DB access
     */
    T update(T model) throws CFException;

    /**
     * Get model by ID
     *
     * @param id identifier of the model
     * @return model or {@code null}
     * @throws CFException error while executing DB access
     */
    T get(int id) throws CFException;

    /**
     * Delete existing model by id
     *
     * @param id identifier of the model
     * @throws CFException error while executing DB access
     */
    void delete(Integer id) throws CFException;
}

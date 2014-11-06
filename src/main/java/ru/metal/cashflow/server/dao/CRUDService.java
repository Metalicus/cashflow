package ru.metal.cashflow.server.dao;

import ru.metal.cashflow.server.exception.CFException;

/**
 * Interface for Create/Read/Update/Delete services
 */
public interface CRUDService<T> {

    /**
     * Insert new model to database
     *
     * @param model new model
     */
    void insert(T model) throws CFException;

    /**
     * Update exisiting model
     *
     * @param model existing model
     */
    void update(T model) throws CFException;

    /**
     * Get model by ID
     *
     * @param id identifier of the model
     * @return model or {@code null}
     */
    T get(Integer id) throws CFException;

    /**
     * Delete existing model by id
     *
     * @param id identifier of the model
     */
    void delete(Integer id) throws CFException;
}

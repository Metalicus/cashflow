package ru.metal.cashflow.server.service;

import ru.metal.cashflow.server.exception.CFException;

import java.util.List;

/**
 * Interface for Create/Read/Update/Delete services
 */
public interface CRUDService<T> {

    /**
     * Get list of all objects
     *
     * @throws CFException error while executing DB access
     */
    List<T> list() throws CFException;

    /**
     * Insert new model to database
     *
     * @param model new model
     * @throws CFException error while executing DB access
     */
    void insert(T model) throws CFException;

    /**
     * Update exisiting model
     *
     * @param model existing model
     * @throws CFException error while executing DB access
     */
    void update(T model) throws CFException;

    /**
     * Get model by ID
     *
     * @param id identifier of the model
     * @return model or {@code null}
     * @throws CFException error while executing DB access
     */
    T get(Integer id) throws CFException;

    /**
     * Delete existing model by id
     *
     * @param id identifier of the model
     * @throws CFException error while executing DB access
     */
    void delete(Integer id) throws CFException;
}

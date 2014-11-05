package ru.metal.cashflow.server.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Operation;

/**
 * Cash flow control dao service
 */
@Repository
public class OperationsDAO implements CRUDService<Operation> {

    private static final Log logger = LogFactory.getLog(OperationsDAO.class);

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private AccountDAO accountDAO;

    @Override
    public void insert(Operation model) throws CFException {

    }

    @Override
    public void update(Operation model) throws CFException {

    }

    @Override
    public Operation get(Integer id) throws CFException {
        return null;
    }

    @Override
    public void delete(Integer id) throws CFException {

    }
}

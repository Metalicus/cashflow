package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.dao.OperationsDAO;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Operation;

import java.util.List;

@Service
public class OperationService implements CRUDService<Operation> {

    @Autowired
    private OperationsDAO operationsDAO;

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public List<Operation> list() throws CFException {
        return operationsDAO.getOperations();
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void insert(Operation model) throws CFException {
        operationsDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Operation model) throws CFException {
        operationsDAO.update(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Operation get(Integer id) throws CFException {
        return operationsDAO.get(id);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        operationsDAO.delete(id);
    }
}

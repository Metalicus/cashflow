package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.dao.OperationsDAO;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.CrossCurrency;
import ru.metal.cashflow.server.model.Operation;

import java.math.BigDecimal;
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
        if (!model.sameCurrency()) {
            model.setCrossCurrency(createCrossCurrency(model));
        }

        operationsDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Operation model) throws CFException {
        //final Operation old = operationsDAO.get(model.getId()); todo
        //if our operation was a crosscurrency operation and now it's not, we should find old one and delete it
        // but if our operation become crosscurrency we should create it

        operationsDAO.update(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Operation get(int id) throws CFException {
        return operationsDAO.get(id);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        operationsDAO.delete(id);
    }

    /**
     * We need to create CrossCurrency if this is operation where account currency not equal to operation's currency
     *
     * @param operation operation
     */
    private CrossCurrency createCrossCurrency(Operation operation) {
        final CrossCurrency crossCurrency = new CrossCurrency();

        // we can calculate exchange rate only if we have information about money
        if (operation.getMoneyWas() != null && operation.getMoneyBecome() != null) {
            BigDecimal crossCurrencyAmount = operation.getMoneyWas().subtract(operation.getMoneyBecome()).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (crossCurrencyAmount.signum() == -1)
                crossCurrencyAmount = crossCurrencyAmount.negate();

            crossCurrency.setAmount(crossCurrencyAmount);
            crossCurrency.setExchangeRate(crossCurrencyAmount.divide(operation.getAmount(), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            crossCurrency.setAmount(BigDecimal.ZERO);
            crossCurrency.setExchangeRate(BigDecimal.ZERO);
        }

        return crossCurrency;
    }
}

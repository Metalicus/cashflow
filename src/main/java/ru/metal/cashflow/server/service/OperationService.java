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
    public Operation insert(Operation model) throws CFException {
        if (!model.sameCurrency()) {
            model.setCrossCurrency(createCrossCurrency(model));
        }

        return operationsDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Operation update(Operation model) throws CFException {
        final Operation oldOperation = operationsDAO.get(model.getId());

        if (!oldOperation.sameCurrency()) { // operation was cross-currency
            if (!model.sameCurrency()) { // updated operation still cross-currency, just update it
                calculateCrossCurrency(model.getCrossCurrency(), model);
            } else { // updated operation became same currency operation, delete old cross-currency object
                model.setCrossCurrency(null);
            }
        } else { // old operation was same currency operation
            if (!model.sameCurrency()) { // updated operation became cross-currency, create cross currency to it
                model.setCrossCurrency(createCrossCurrency(model));
            }
        }

        return operationsDAO.update(model);
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
        calculateCrossCurrency(crossCurrency, operation);
        return crossCurrency;
    }

    private void calculateCrossCurrency(CrossCurrency crossCurrency, Operation operation) {
        // we can calculate exchange rate only if we have information about money
        if (operation.getMoneyWas() != null && operation.getMoneyBecome() != null) {
            BigDecimal crossCurrencyAmount = operation.getMoneyWas().subtract(operation.getMoneyBecome()).setScale(2, BigDecimal.ROUND_HALF_UP);
            crossCurrencyAmount = crossCurrencyAmount.abs();

            crossCurrency.setAmount(crossCurrencyAmount);
            crossCurrency.setExchangeRate(crossCurrencyAmount.divide(operation.getAmount(), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            crossCurrency.setAmount(BigDecimal.ZERO);
            crossCurrency.setExchangeRate(BigDecimal.ZERO);
        }
    }
}

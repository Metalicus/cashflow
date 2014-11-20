package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.dao.AccountDAO;
import ru.metal.cashflow.server.dao.OperationsDAO;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.CrossCurrency;
import ru.metal.cashflow.server.model.Operation;
import ru.metal.cashflow.server.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OperationService implements CRUDService<Operation> {

    @Autowired
    private OperationsDAO operationsDAO;
    @Autowired
    private AccountDAO accountDAO;

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public List<Operation> list() throws CFException {
        return operationsDAO.getOperations();
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Operation insert(Operation model) throws CFException {
        // foolproof
        if (model.getId() != null)
            throw new CFException("Operation already exists");

        // calculate cross-currency
        if (!model.sameCurrency())
            model.setCrossCurrency(createCrossCurrency(model));

        // we believe in moneyBecome variable, because person who created this operations knows better
        final Account account = accountDAO.get(model.getAccount().getId());
        account.setBalance(model.getMoneyBecome());
        accountDAO.update(account);

        if (model.getTransfer() != null) {
            // if this is "TRANSFER" we update the account where the money went
            final Account transferAccount = accountDAO.get(model.getTransfer().getTo().getId());
            transferAccount.setBalance(transferAccount.getBalance().add(model.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
            accountDAO.update(transferAccount);
        }

        return operationsDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Operation update(Operation model) throws CFException {
        final Operation oldOperation = operationsDAO.get(model.getId());

        // foolproof
        if (!model.getType().equals(oldOperation.getType()))
            throw new CFException("The operation can not change its type");

        // calculate cross-currency
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

        // before save new data we need revert money flow in old operation and calculate new
        final Account oldAccount = oldOperation.getAccount();
        final Account newAccount = accountDAO.get(model.getAccount().getId());
        switch (model.getType()) {
            case EXPENSE:
                // revert old expense operation
                oldAccount.setBalance(oldAccount.getBalance().add(oldOperation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountDAO.update(oldAccount);

                // update with new expense operation
                newAccount.setBalance(newAccount.getBalance().subtract(model.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountDAO.update(newAccount);

                break;
            case INCOME:
                // revert old income operation
                oldAccount.setBalance(oldAccount.getBalance().subtract(oldOperation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountDAO.update(oldAccount);

                // update with new income operation
                newAccount.setBalance(newAccount.getBalance().add(model.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountDAO.update(newAccount);

                break;
            case TRANSFER:
                final Transfer oldTransfer = oldOperation.getTransfer();

                // revert old expense and income
                oldAccount.setBalance(oldAccount.getBalance().add(oldOperation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                oldTransfer.getTo().setBalance(oldTransfer.getTo().getBalance().subtract(oldOperation.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountDAO.update(oldAccount);
                accountDAO.update(oldTransfer.getTo());

                // update with new "TRANSFER"
                final Account accountTransfer = accountDAO.get(model.getTransfer().getTo().getId());
                newAccount.setBalance(newAccount.getBalance().subtract(model.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountTransfer.setBalance(accountTransfer.getBalance().add(model.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountDAO.update(newAccount);
                accountDAO.update(accountTransfer);

                break;
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
        final BigDecimal crossCurrencyAmount = operation.getMoneyWas().subtract(operation.getMoneyBecome()).setScale(2, BigDecimal.ROUND_HALF_UP).abs();
        crossCurrency.setAmount(crossCurrencyAmount);
        crossCurrency.setExchangeRate(crossCurrencyAmount.divide(operation.getAmount(), 2, BigDecimal.ROUND_HALF_UP));
    }
}

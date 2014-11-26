package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.model.CrossCurrency;
import ru.metal.cashflow.server.model.Operation;
import ru.metal.cashflow.server.model.Transfer;
import ru.metal.cashflow.server.repository.OperationRepository;
import ru.metal.cashflow.server.request.FilterRequest;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OperationService implements CRUDService<Operation> {

    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private AccountService accountService;

    @Transactional(readOnly = true)
    public List<Operation> list(Pageable pageable, FilterRequest filterRequest) {
        if (filterRequest != null) {
            // обрабатываем фильтры
            final FilterRequest.Filter categoryFilter = filterRequest.getFilter("category");
            if (categoryFilter != null)
                return operationRepository.findByCategoryId(Integer.parseInt(categoryFilter.getValue()), pageable).getContent();
        }

        return operationRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Operation insert(Operation model) throws CFException {
        // foolproof
        if (model.getId() != null)
            throw new CFException("Operation already exists");

        if (model.getAccount() == null)
            throw new CFException("Account can not be null");

        if (model.getCurrency() == null)
            throw new CFException("Currency can not be null");

        if (model.getCategory() == null)
            throw new CFException("Category can not be null");

        // calculate cross-currency
        if (!model.sameCurrency())
            model.setCrossCurrency(createCrossCurrency(model));

        // we believe in moneyBecome variable, because person who created this operations knows better
        final Account account = accountService.get(model.getAccount().getId());
        account.setBalance(model.getMoneyBecome());
        accountService.update(account);

        if (model.getTransfer() != null) {
            if (model.getTransfer().getAmount() == null)
                model.getTransfer().setAmount(model.getAmount());

            // if this is "TRANSFER" we update the account where the money went
            final Account transferAccount = accountService.get(model.getTransfer().getTo().getId());
            transferAccount.setBalance(transferAccount.getBalance().add(model.getTransfer().getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
            accountService.update(transferAccount);
        }

        return operationRepository.saveAndFlush(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Operation update(Operation model) throws CFException {
        if (model.getAccount() == null)
            throw new CFException("Account can not be null");

        if (model.getCurrency() == null)
            throw new CFException("Currency can not be null");

        if (model.getCategory() == null)
            throw new CFException("Category can not be null");

        final Operation oldOperation = get(model.getId());

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
        final Account newAccount = accountService.get(model.getAccount().getId());
        switch (model.getType()) {
            case EXPENSE:
                // revert old expense operation
                oldAccount.setBalance(oldAccount.getBalance().add(oldOperation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountService.update(oldAccount);

                // update with new expense operation
                newAccount.setBalance(newAccount.getBalance().subtract(model.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountService.update(newAccount);

                break;
            case INCOME:
                // revert old income operation
                oldAccount.setBalance(oldAccount.getBalance().subtract(oldOperation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountService.update(oldAccount);

                // update with new income operation
                newAccount.setBalance(newAccount.getBalance().add(model.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountService.update(newAccount);

                break;
            case TRANSFER:
                final Transfer oldTransfer = oldOperation.getTransfer();

                // revert old expense and income
                oldAccount.setBalance(oldAccount.getBalance().add(oldOperation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                oldTransfer.getTo().setBalance(oldTransfer.getTo().getBalance().subtract(oldTransfer.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountService.update(oldAccount);
                accountService.update(oldTransfer.getTo());

                // update with new "TRANSFER"
                final Account accountTransfer = accountService.get(model.getTransfer().getTo().getId());
                newAccount.setBalance(newAccount.getBalance().subtract(model.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountTransfer.setBalance(accountTransfer.getBalance().add(model.getTransfer().getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountService.update(newAccount);
                accountService.update(accountTransfer);

                break;
        }

        return operationRepository.saveAndFlush(model);
    }

    @Override
    @Transactional(readOnly = true)
    public Operation get(int id) {
        return operationRepository.findOne(id);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        final Operation operation = get(id);
        if (operation == null) {
            throw new CFException("Object not found");
        }

        final Account account = operation.getAccount();
        switch (operation.getType()) {
            case INCOME:
                account.setBalance(account.getBalance().subtract(operation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                break;
            case EXPENSE:
                account.setBalance(account.getBalance().add(operation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));
                break;
            case TRANSFER:
                account.setBalance(account.getBalance().add(operation.getMoneyInAccountCurrency()).setScale(2, BigDecimal.ROUND_HALF_UP));

                final Transfer transfer = operation.getTransfer();
                final Account accountTo = transfer.getTo();
                accountTo.setBalance(accountTo.getBalance().subtract(transfer.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                accountService.update(accountTo);

                break;
        }
        accountService.update(account);

        operationRepository.delete(id);
    }

    /**
     * We need to create CrossCurrency if this operation's account's currency not equal to operation's currency
     *
     * @param operation operation
     */
    private CrossCurrency createCrossCurrency(Operation operation) {
        final CrossCurrency crossCurrency = new CrossCurrency();
        calculateCrossCurrency(crossCurrency, operation);
        return crossCurrency;
    }

    /**
     * Recalculate CrossCurrency values
     *
     * @param crossCurrency cross-currency object
     * @param operation     operation
     */
    private void calculateCrossCurrency(CrossCurrency crossCurrency, Operation operation) {
        final BigDecimal crossCurrencyAmount = operation.getMoneyWas().subtract(operation.getMoneyBecome()).setScale(2, BigDecimal.ROUND_HALF_UP).abs();
        crossCurrency.setAmount(crossCurrencyAmount);
        crossCurrency.setExchangeRate(crossCurrencyAmount.divide(operation.getAmount(), 2, BigDecimal.ROUND_HALF_UP));
    }
}

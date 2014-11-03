package ru.metal.cashflow.server.service;

import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;

/**
 * Service to work with accounts
 */
public class AccountService implements CRUDService<Account> {

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void insert(Account model) {

    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Account model) {

    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Account get(Integer id) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) {

    }
}

package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.dao.AccountDAO;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;

import java.util.List;

@Service
public class AccountService implements CRUDService<Account> {

    @Autowired
    private AccountDAO accountDAO;

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public List<Account> list() throws CFException {
        return accountDAO.getAccounts();
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Account insert(Account model) throws CFException {
        // foolproof
        if (model.getId() != null)
            throw new CFException("Account already exists");

        return accountDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Account update(Account model) throws CFException {
        // foolproof
        final Account accountOld = get(model.getId());
        if (!accountOld.getCurrency().equals(model.getCurrency()))
            throw new CFException("You can't change account's currency!");

        return accountDAO.update(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Account get(int id) throws CFException {
        return accountDAO.get(id);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        accountDAO.delete(id);
    }
}

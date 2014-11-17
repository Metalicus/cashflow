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
    public void insert(Account model) throws CFException {
        accountDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Account model) throws CFException {
        accountDAO.update(model);
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

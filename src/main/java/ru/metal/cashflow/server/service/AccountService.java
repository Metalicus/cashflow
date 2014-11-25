package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Account;
import ru.metal.cashflow.server.repository.AccountRepository;

import java.util.List;

@Service
public class AccountService implements CRUDService<Account> {

    @Autowired
    private AccountRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Account> list() {
        return repository.findAll();
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Account insert(Account model) throws CFException {
        // foolproof
        if (model.getId() != null)
            throw new CFException("Account already exists");

        return repository.saveAndFlush(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public Account update(Account model) throws CFException {
        // foolproof
        final Account accountOld = get(model.getId());
        if (!accountOld.getCurrency().equals(model.getCurrency()))
            throw new CFException("You can't change account's currency!");

        return repository.saveAndFlush(model);
    }

    @Override
    @Transactional(readOnly = true)
    public Account get(int id) {
        return repository.findOne(id);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        repository.delete(id);
    }
}

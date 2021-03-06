package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.business.Account;
import ru.metal.cashflow.server.repository.AccountRepository;
import ru.metal.cashflow.server.request.FilterRequest;

@Service("accountService")
public class AccountService implements CRUDService<Account> {

    @Autowired
    AccountRepository repository;

    @Transactional(readOnly = true)
    public Page<Account> list(Pageable pageable, FilterRequest filterRequest) {
        return repository.findAll(pageable);
    }

    @Transactional(rollbackFor = CFException.class)
    public Account insert(Account model) throws CFException {
        // foolproof
        if (model.getId() != null)
            throw new CFException("Account already exists");

        return repository.saveAndFlush(model);
    }

    @Transactional(rollbackFor = CFException.class)
    public Account update(Account model) throws CFException {
        // foolproof
        final Account accountOld = get(model.getId());
        if (!accountOld.getCurrency().equals(model.getCurrency()))
            throw new CFException("You can't change account's currency!");

        return repository.saveAndFlush(model);
    }

    @Transactional(readOnly = true)
    public Account get(int id) {
        return repository.findOne(id);
    }

    @Transactional
    public void delete(Integer id) {
        repository.delete(id);
    }
}

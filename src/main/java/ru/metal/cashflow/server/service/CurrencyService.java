package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.dao.CurrencyDAO;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.Currency;

import java.util.List;

@Service
public class CurrencyService implements CRUDService<Currency> {

    @Autowired
    CurrencyDAO currencyDAO;

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public List<Currency> list() throws CFException {
        return currencyDAO.getCurrencies();
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void insert(Currency model) throws CFException {
        currencyDAO.insert(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void update(Currency model) throws CFException {
        currencyDAO.update(model);
    }

    @Override
    @Transactional(rollbackFor = CFException.class, readOnly = true)
    public Currency get(int id) throws CFException {
        return currencyDAO.get(id);
    }

    @Override
    @Transactional(rollbackFor = CFException.class)
    public void delete(Integer id) throws CFException {
        currencyDAO.delete(id);
    }
}

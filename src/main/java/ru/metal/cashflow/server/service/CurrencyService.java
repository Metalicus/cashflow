package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.model.business.Currency;
import ru.metal.cashflow.server.repository.CurrencyRepository;
import ru.metal.cashflow.server.request.FilterRequest;

@Service("currencyService")
public class CurrencyService implements CRUDService<Currency> {

    @Autowired
    CurrencyRepository currencyRepository;

    @Transactional(readOnly = true)
    public Page<Currency> list(Pageable pageable, FilterRequest filterRequest) {
        return currencyRepository.findAll(pageable);
    }

    @Transactional
    public Currency insert(Currency model) {
        return currencyRepository.saveAndFlush(model);
    }

    @Transactional
    public Currency update(Currency model) {
        return currencyRepository.saveAndFlush(model);
    }

    @Transactional(readOnly = true)
    public Currency get(int id) {
        return currencyRepository.findOne(id);
    }

    @Transactional
    public void delete(Integer id) {
        currencyRepository.delete(id);
    }
}

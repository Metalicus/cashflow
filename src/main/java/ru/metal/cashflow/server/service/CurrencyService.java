package ru.metal.cashflow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.server.repository.CurrencyRepository;

import java.util.List;

@Service
public class CurrencyService implements CRUDService<Currency> {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Currency> list(Pageable pageable) {
        return currencyRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional
    public Currency insert(Currency model) {
        return currencyRepository.saveAndFlush(model);
    }

    @Override
    @Transactional
    public Currency update(Currency model) {
        return currencyRepository.saveAndFlush(model);
    }

    @Override
    @Transactional(readOnly = true)
    public Currency get(int id) {
        return currencyRepository.findOne(id);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        currencyRepository.delete(id);
    }
}

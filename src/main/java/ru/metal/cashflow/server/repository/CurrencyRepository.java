package ru.metal.cashflow.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.metal.cashflow.server.model.business.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}

package ru.metal.cashflow.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.metal.cashflow.server.model.business.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}

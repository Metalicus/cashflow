package ru.metal.cashflow.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.metal.cashflow.server.model.Operation;

public interface OperationRepository extends JpaRepository<Operation, Integer> {
}

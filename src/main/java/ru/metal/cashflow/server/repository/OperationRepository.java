package ru.metal.cashflow.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.metal.cashflow.server.model.Operation;

public interface OperationRepository extends JpaRepository<Operation, Integer> {

    /**
     * Find operation by category using page restrictions and sorting
     *
     * @param id       id of category
     * @param pageable page and sorting restrinctions
     * @return page with operations
     */
    Page<Operation> findByCategoryId(Integer id, Pageable pageable);
}

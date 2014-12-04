package ru.metal.cashflow.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.metal.cashflow.server.model.business.Operation;

import java.util.Date;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Integer> {

    /**
     * Find operation by category using page restrictions and sorting
     *
     * @param id       id of category
     * @param pageable page and sorting restrinctions
     * @return page with operations
     */
    Page<Operation> findByCategoryId(Integer id, Pageable pageable);

    /**
     * Collect all not Cross Currency operations within date period
     *
     * @param dateFrom date from
     * @param dateTo   date to
     * @return operation collection
     */
    @Query("select sum(amount), currency.id, category.id, type from Operation where date between ?1 and ?2 and crossCurrency is null group by currency.id, category.id, type")
    List<Object[]> findMonthlyReport(Date dateFrom, Date dateTo);

    /**
     * Collect all Cross Currency operations within date period
     *
     * @param dateFrom date from
     * @param dateTo   date to
     * @return operation collection
     */
    @Query("select sum(cc.amount), cur.id, cat.id, op.type " +
            "from Operation as op " +
            "join op.crossCurrency as cc " +
            "join op.account as acc " +
            "join op.category as cat " +
            "join acc.currency as cur " +
            "where op.date between ?1 and ?2 and op.crossCurrency is not null " +
            "group by cur.id, cat.id, op.type")
    List<Object[]> findMonthlyCrossCurrencyReport(Date dateFrom, Date dateTo);
}

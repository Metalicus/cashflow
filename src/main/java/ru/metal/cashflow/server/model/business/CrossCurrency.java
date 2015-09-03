package ru.metal.cashflow.server.model.business;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Inforamition about cross currency transactions
 */
@Entity
@Table
public class CrossCurrency {

    private Integer id;
    private BigDecimal exchangeRate = BigDecimal.ZERO;
    private BigDecimal amount = BigDecimal.ZERO;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Exchange rate for operation
     */
    @Column(nullable = false)
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    /**
     * @return amount of the money spent in account's currency
     */
    @Column(nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossCurrency that = (CrossCurrency) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

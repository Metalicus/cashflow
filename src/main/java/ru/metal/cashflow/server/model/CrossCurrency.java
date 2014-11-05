package ru.metal.cashflow.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Inforamition about cross currency transactions
 */
@Entity
public class CrossCurrency {

    private Integer id;
    private BigDecimal exchangeRate;
    private BigDecimal amount;

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
     * @return amount of the money spent in different currency
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
        if (!(o instanceof CrossCurrency)) return false;

        CrossCurrency that = (CrossCurrency) o;

        if (amount != null ? amount.compareTo(that.amount) != 0 : that.amount != null) return false;
        if (exchangeRate != null ? exchangeRate.compareTo(that.exchangeRate) != 0 : that.exchangeRate != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (exchangeRate != null ? exchangeRate.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}

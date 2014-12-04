package ru.metal.cashflow.server.model.business;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Transfer information
 */
@Entity
public class Transfer {

    private Integer id;
    private Account to;
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
     * @return account to which money is transferred
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    public Account getTo() {
        return to;
    }

    public void setTo(Account to) {
        this.to = to;
    }

    /**
     * @return amount of money recieved by account-to. It's always in account-to's currency
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

        Transfer transfer = (Transfer) o;

        if (amount != null ? amount.compareTo(transfer.amount) != 0 : transfer.amount != null) return false;
        if (id != null ? !id.equals(transfer.id) : transfer.id != null) return false;
        if (to != null ? !to.equals(transfer.to) : transfer.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}

package ru.metal.cashflow.server.model.business;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table
public class Account {

    private Integer id;
    private String name;
    private Currency currency;
    private BigDecimal balance = BigDecimal.ZERO;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name of the account
     */
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return currency of the account
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return current amount of cash
     */
    @Column(nullable = false)
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal currentMoney) {
        this.balance = currentMoney;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != null ? !id.equals(account.id) : account.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

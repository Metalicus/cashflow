package ru.metal.cashflow.server.model.business;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
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
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        if (balance != null ? balance.compareTo(account.balance) != 0 : account.balance != null) return false;
        if (currency != null ? !currency.equals(account.currency) : account.currency != null) return false;
        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (name != null ? !name.equals(account.name) : account.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        return result;
    }
}

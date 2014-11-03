package ru.metal.cashflow.server.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Account {

    private Integer id;
    private String name;
    private Currency currency;
    private BigDecimal balance;

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
    @Column(nullable = false)
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
}

package ru.metal.cashflow.server.model.business;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Operation with money and accounts
 */
@Entity
@Table
public class Operation {

    public enum FlowType {
        EXPENSE, INCOME, TRANSFER
    }

    private Integer id;
    private Date date;
    private Account account;
    private Currency currency;
    private Category category;
    private FlowType type = FlowType.EXPENSE;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal moneyWas = BigDecimal.ZERO;
    private BigDecimal moneyBecome = BigDecimal.ZERO;
    private String info;

    private Transfer transfer;
    private CrossCurrency crossCurrency;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return date and time of the operation
     */
    @Column(nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return account's operation
     * @see Account
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return currency of the operations. It may be different from account currency
     * @see Currency
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
     * @return category of the operation
     * @see Category
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * @return type of the operation. It can be 'INCOME' if you put money into the account,
     * or 'EXPENSE' if you spend money. And it can be 'TRANSFER' then you transfer money from one account to another.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    public FlowType getType() {
        return type;
    }

    public void setType(FlowType type) {
        this.type = type;
    }

    /**
     * @return amount of the money spent in the operations's currency
     */
    @Column(nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the total amount of the money on the account before this operation
     */
    @Column(nullable = false)
    public BigDecimal getMoneyWas() {
        return moneyWas;
    }

    public void setMoneyWas(BigDecimal moneyWas) {
        this.moneyWas = moneyWas;
    }

    /**
     * @return the total amount of the money on the account after this operation
     */
    @Column(nullable = false)
    public BigDecimal getMoneyBecome() {
        return moneyBecome;
    }

    public void setMoneyBecome(BigDecimal moneyBecome) {
        this.moneyBecome = moneyBecome;
    }

    /**
     * @return additional information about this transcaction. Can be {@code null}
     */
    @Column(nullable = true)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return if this is cross currency transaction, then currency of the account and currency of this operation
     * is not the same creates CrossCurrency object, wich contains additional information about this operation,
     * for example, exchange rate for currency. Can be {@code null}
     * @see CrossCurrency
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(nullable = true)
    public CrossCurrency getCrossCurrency() {
        return crossCurrency;
    }

    public void setCrossCurrency(CrossCurrency crossCurrency) {
        this.crossCurrency = crossCurrency;
    }

    /**
     * @return if this is a "TRANSFER" operation this object will be contain information about transfer
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(nullable = true)
    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    /**
     * Check if this operation is same currency with account
     *
     * @return {@code true} if operation's currency and account's currency is the same
     */
    public boolean sameCurrency() {
        return account == null || currency == null || account.getCurrency() == null || account.getCurrency().getId().equals(currency.getId());
    }

    /**
     * @return get operation's money in account currency
     */
    @Transient
    @JsonIgnore
    public BigDecimal getMoneyInAccountCurrency() {
        if (crossCurrency == null)
            return amount;

        return crossCurrency.getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (id != null ? !id.equals(operation.id) : operation.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

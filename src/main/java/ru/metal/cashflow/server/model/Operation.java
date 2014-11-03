package ru.metal.cashflow.server.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.metal.cashflow.server.hibernate.LocalDateTimeUserType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Operation with money and accounts
 */
@Entity
@TypeDef(name = "localDateTimeType",
        defaultForType = LocalDateTime.class,
        typeClass = LocalDateTimeUserType.class)
public class Operation {

    public enum FlowType {
        OUTCOME, INCOME, TRANSFER
    }

    private Integer id;
    private LocalDateTime date;
    private Account account;
    private Currency currency;
    private Category category;
    private FlowType type;
    private BigDecimal amount;
    private BigDecimal moneyWas;
    private BigDecimal moneyBecome;
    private String info;

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
    @Type(type = "localDateTimeType")
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * @return account's operation
     * @see Account
     */
    @ManyToOne
    @Column(nullable = false)
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
    @Column(nullable = false)
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
    @Column(nullable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * @return type of the operation. It can be 'INCOME' if you put money into the account,
     * or 'OUTCOME' if you spend money. And it can be 'TRANSFER' then you transfer money from one account to another,
     * in that case will be creating two operations with 'TRANSFER' type: one for one account and for second one
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('OUTCOME','INCOME','TRANSFER')")
    public FlowType getType() {
        return type;
    }

    public void setType(FlowType type) {
        this.type = type;
    }

    /**
     * @return total amount of money for this operation. How much you spend or how much you get
     */
    @Column(nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the total amount of the money on the account before this operation. Can be {@code null}
     */
    @Column(nullable = true)
    public BigDecimal getMoneyWas() {
        return moneyWas;
    }

    public void setMoneyWas(BigDecimal moneyWas) {
        this.moneyWas = moneyWas;
    }

    /**
     * @return the total amount of the money on the account after this operation. Can be {@code null}
     */
    @Column(nullable = true)
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
    @ManyToOne
    @Column(nullable = true)
    public CrossCurrency getCrossCurrency() {
        return crossCurrency;
    }

    public void setCrossCurrency(CrossCurrency crossCurrency) {
        this.crossCurrency = crossCurrency;
    }
}

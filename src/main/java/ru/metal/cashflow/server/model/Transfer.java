package ru.metal.cashflow.server.model;

import javax.persistence.*;

/**
 * Transfer information
 */
@Entity
public class Transfer {

    private Integer id;
    private Account to;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        if (id != null ? !id.equals(transfer.id) : transfer.id != null) return false;
        if (to != null ? !to.equals(transfer.to) : transfer.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}

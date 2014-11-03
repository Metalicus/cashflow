package ru.metal.cashflow.server.service;

import org.hibernate.SessionFactory;

/**
 * Flow control service
 */
public class CashFlowService {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

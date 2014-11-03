package ru.metal.cashflow.server.service;

import org.hibernate.SessionFactory;

/**
 * Cash flow control service
 */
public class OperationsService {

    private SessionFactory sessionFactory;


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

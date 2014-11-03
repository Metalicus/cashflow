package ru.metal.cashflow.server.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.metal.cashflow.server.SpringTestCase;
import ru.metal.cashflow.server.model.Currency;
import ru.metal.cashflow.utils.HibernateUtilsTest;

import static org.junit.Assert.*;

public class CurrencyServiceTest extends SpringTestCase {

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void saveTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        assertNull(currency.getId());

        currencyService.insert(currency);
        assertNotNull(currency.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));
    }

    @Test
    public void updateTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("currency");
        assertNull(currency.getId());

        currencyService.insert(currency);
        final Integer id = currency.getId();
        assertNotNull(id);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        currency.setName("new currency name");
        currencyService.update(currency);
        // id doesnt change, it's the same object
        assertEquals(id, currency.getId());
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        assertEquals("new currency name", ((Currency) sessionFactory.getCurrentSession().get(Currency.class, id)).getName());
    }

    @Test
    public void getTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyService.insert(currency);

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        final Currency currencyFromDB = currencyService.get(currency.getId());
        assertEquals(currency, currencyFromDB);
    }

    @Test
    public void deleteTest() throws Exception {
        final Currency currency = new Currency();
        currency.setName("new currency");
        currencyService.insert(currency);
        assertEquals(1, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));

        // clear session to perform re-read from database
        sessionFactory.getCurrentSession().clear();

        currencyService.delete(currency.getId());
        assertEquals(0, HibernateUtilsTest.executeCount(sessionFactory.getCurrentSession(), Currency.class));
    }
}

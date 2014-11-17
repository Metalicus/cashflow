package ru.metal.cashflow.server.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OperationTest {

    @Test
    public void sameCurrencyTest() throws Exception {
        final Currency currency1 = new Currency();
        currency1.setId(1);
        final Currency currency2 = new Currency();
        currency2.setId(2);

        final Account account = new Account();
        account.setId(1);

        final CrossCurrency crossCurrency = new CrossCurrency();
        crossCurrency.setId(1);

        final Operation operation = new Operation();
        operation.setCrossCurrency(crossCurrency);
        assertTrue(operation.sameCurrency());

        operation.setCrossCurrency(null);
        assertTrue(operation.sameCurrency());

        operation.setCurrency(currency1);
        assertTrue(operation.sameCurrency());

        operation.setAccount(account);
        assertTrue(operation.sameCurrency());

        account.setCurrency(currency2);
        assertFalse(operation.sameCurrency());

        account.setCurrency(currency1);
        assertTrue(operation.sameCurrency());
    }
}

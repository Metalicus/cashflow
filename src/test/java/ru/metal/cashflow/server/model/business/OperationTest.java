package ru.metal.cashflow.server.model.business;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

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

    @Test
    public void getMoneyInAccountCurrencyTest() throws Exception {
        final Operation operation = new Operation();
        operation.setAmount(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, operation.getMoneyInAccountCurrency());

        final CrossCurrency crossCurrency = new CrossCurrency();
        crossCurrency.setAmount(BigDecimal.ONE);
        operation.setCrossCurrency(crossCurrency);
        assertEquals(BigDecimal.ONE, operation.getMoneyInAccountCurrency());
    }
}

package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.service.CurrencyServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CurrencyServiceTest.class
})
public class AllTests {
}

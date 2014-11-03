package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.service.AccountServiceTest;
import ru.metal.cashflow.server.service.CategoryServiceTest;
import ru.metal.cashflow.server.service.CurrencyServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CurrencyServiceTest.class,
        AccountServiceTest.class,
        CategoryServiceTest.class
})
public class AllTests {
}

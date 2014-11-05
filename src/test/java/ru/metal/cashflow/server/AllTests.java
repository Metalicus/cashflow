package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.service.AccountDAOTest;
import ru.metal.cashflow.server.service.CategoryDAOTest;
import ru.metal.cashflow.server.service.CurrencyDAOTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CurrencyDAOTest.class,
        AccountDAOTest.class,
        CategoryDAOTest.class
})
public class AllTests {
}

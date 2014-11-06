package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.dao.AccountDAOTest;
import ru.metal.cashflow.server.dao.CategoryDAOTest;
import ru.metal.cashflow.server.dao.CurrencyDAOTest;
import ru.metal.cashflow.server.dao.OperationsDAOTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CurrencyDAOTest.class,
        AccountDAOTest.class,
        CategoryDAOTest.class,
        OperationsDAOTest.class
})
public class AllTests {
}

package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.dao.AccountDAOTest;
import ru.metal.cashflow.server.dao.CategoryDAOTest;
import ru.metal.cashflow.server.dao.CurrencyDAOTest;
import ru.metal.cashflow.server.dao.OperationsDAOTest;
import ru.metal.cashflow.server.model.OperationTest;
import ru.metal.cashflow.server.service.OperationServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        OperationTest.class,
        OperationServiceTest.class,
        ControllerTests.class,
        CurrencyDAOTest.class,
        AccountDAOTest.class,
        CategoryDAOTest.class,
        OperationsDAOTest.class
})
public class AllTests {
}

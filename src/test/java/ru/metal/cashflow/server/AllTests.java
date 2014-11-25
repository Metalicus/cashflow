package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.model.OperationTest;
import ru.metal.cashflow.server.service.AccountServiceTest;
import ru.metal.cashflow.server.service.CategoryServiceTest;
import ru.metal.cashflow.server.service.CurrencyServiceTest;
import ru.metal.cashflow.server.service.OperationServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        OperationTest.class,
        OperationServiceTest.class,
        CurrencyServiceTest.class,
        AccountServiceTest.class,
        OperationServiceTest.class,
        CategoryServiceTest.class,
        ControllerTests.class
})
public class AllTests {
}

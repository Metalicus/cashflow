package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.model.business.OperationTest;
import ru.metal.cashflow.server.service.*;
import ru.metal.cashflow.server.utils.DateUtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        OperationTest.class,
        OperationServiceTest.class,
        CurrencyServiceTest.class,
        AccountServiceTest.class,
        OperationServiceTest.class,
        CategoryServiceTest.class,
        ControllerTests.class,
        DateUtilsTest.class,
        ReportServiceTest.class
})
public class AllTests {
}

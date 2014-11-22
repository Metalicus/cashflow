package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.controller.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RestCRUDControllerTest.class,
        OperationControllerTest.class,
        CategoryControllerTest.class,
        CurrencyControllerTest.class,
        AccountControllerTest.class
})
public class ControllerTests {
}

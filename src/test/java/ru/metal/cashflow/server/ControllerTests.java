package ru.metal.cashflow.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.metal.cashflow.server.controller.MainControllerTest;
import ru.metal.cashflow.server.controller.OperationControllerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainControllerTest.class,
        OperationControllerTest.class
})
public class ControllerTests {
}

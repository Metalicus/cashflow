package ru.metal.cashflow.server;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/applicationContext-liquibase.xml",
        "classpath:applicationContext-dataSource-tests.xml",
        "file:src/main/webapp/WEB-INF/applicationContext-hibernate.xml",
        "file:src/main/webapp/WEB-INF/applicationContext-manager.xml",
})
public class SpringTestCase extends AbstractTransactionalJUnit4SpringContextTests {

    @Resource
    protected SessionFactory sessionFactory;

    @BeforeClass
    public static void initLog4j() {
        try {
            final InputStream propertiesStream = SpringTestCase.class.getResourceAsStream("/log4j.properties");
            if (propertiesStream != null) {
                final Properties props = new Properties();
                props.load(propertiesStream);
                PropertyConfigurator.configure(props);
            }
        } catch (IOException ignored) {
        }
    }
}
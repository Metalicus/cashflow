package ru.metal.cashflow.server;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.config.AppConfig;
import ru.metal.cashflow.server.config.TestDataSourceConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestDataSourceConfig.class})
public class SpringTestCase {

    @PersistenceContext
    protected EntityManager entityManager;

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
package ru.metal.cashflow.server;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/applicationContext-liquibase.xml",
        "classpath:applicationContext-dataSource-tests.xml",
        "file:src/main/webapp/WEB-INF/applicationContext-hibernate.xml",
        "file:src/main/webapp/WEB-INF/applicationContext-repository.xml",
        "file:src/main/webapp/WEB-INF/applicationContext-service.xml",
})
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
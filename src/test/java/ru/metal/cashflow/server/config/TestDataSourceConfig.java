package ru.metal.cashflow.server.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TestDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver");
        dataSource.setUrl("jdbc:p6spy:h2:mem:cashflow_test");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        dataSource.setInitialSize(1);
        return dataSource;
    }
}

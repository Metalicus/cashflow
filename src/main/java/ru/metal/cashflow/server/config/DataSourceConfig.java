package ru.metal.cashflow.server.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:C:\\data\\cashflow");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        dataSource.setInitialSize(10);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        return dataSource;
    }
}

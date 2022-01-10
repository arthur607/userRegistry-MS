package com.example.demo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Configuration
public class DataSourceConfig {

    @Value("app.datasource.main.jdbc-url")
    private String url;
    @Value("app.datasource.main.drive-class-name")
    private String driverClassName;
    @Value("app.datasource.main.username")
    private String username;
    @Value("app.datasource.main.password")
    private String password;
    @Value("app.datasource.main.pool-name")
    private String poolName;
    @Value("${app.datasource.main.max-pool-size}")
    private Integer maxPoolSize;
    @Value("${app.datasource.main.min-pool-size}")
    private Integer minPoolSize;
    @Value("${app.datasource.main.max-lifetime}")
    private Integer maxLifetime;
    @Value("${app.datasource.main.validation-timeout}")
    private Integer validationTimeout;

    public DataSource dataSource() throws IOException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setPoolName(poolName);
        hikariConfig.setUsername(extractSecretValue(username));
        hikariConfig.setPassword(extractSecretValue(password));
        hikariConfig.setIdleTimeout(TimeUnit.SECONDS.toMillis(20));
        hikariConfig.setIdleTimeout(TimeUnit.MINUTES.toMillis(30));
        hikariConfig.setMinimumIdle(minPoolSize);
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMaxLifetime(maxLifetime);

        return new HikariDataSource(hikariConfig);
    }

    private static String extractSecretValue(String secret) throws IOException{
        Path secretPath = Path.of(secret);
        return Files.readString(secretPath);
    }
}

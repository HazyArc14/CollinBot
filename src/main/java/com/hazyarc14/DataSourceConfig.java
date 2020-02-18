package com.hazyarc14;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL}")
    private static String databaseUrl;

    public static final Logger log = LoggerFactory.getLogger(Bot.class);

    @Bean
    public DataSource dataSource() throws SQLException {

        if (databaseUrl == null || databaseUrl.isEmpty()) {

            log.info("databaseUrl is null");
            return new HikariDataSource();

        } else {

            log.info("databaseUrl is not null");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(databaseUrl);
            return new HikariDataSource(config);

        }

    }

}

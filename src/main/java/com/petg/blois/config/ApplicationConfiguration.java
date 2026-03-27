package com.petg.blois.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.petg.blois.exception.ServiceException;
import com.petg.blois.util.PGUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({BatchProperties.class, DatabaseProperties.class, DatabaseProdProperties.class})
public class ApplicationConfiguration {
    @Bean
    public HikariDataSource dataSource(DatabaseProperties databaseProperties) {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = String.format(
                databaseProperties.jdbcUrlPattern(),
                databaseProperties.host(),
                databaseProperties.instance(),
                databaseProperties.dbName(),
                databaseProperties.appName()
        );
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(databaseProperties.username());
        hikariConfig.setPassword(databaseProperties.password());
        if (jdbcUrl.toLowerCase().contains("prod")) {
            throw new ServiceException("Connexion vers la prod !");
        }
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public HikariDataSource dataSourceProd(DatabaseProdProperties databaseProdProperties) {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = String.format(
                databaseProdProperties.jdbcUrlPattern(),
                databaseProdProperties.host(),
                databaseProdProperties.instance(),
                databaseProdProperties.dbName(),
                databaseProdProperties.appName()
        );
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(databaseProdProperties.username());
        hikariConfig.setPassword(databaseProdProperties.password());
        return new HikariDataSource(hikariConfig);
    }

    /**
     * Initialise le mapper à utiliser par Spring / Jackson pour la gestion des jsons.
     * {@link PGUtils#createJsonMapper()}
     *
     * @return JsonMapper
     */
    @Bean
    public JsonMapper jsonMapper() {
        return PGUtils.createJsonMapper();
    }
}

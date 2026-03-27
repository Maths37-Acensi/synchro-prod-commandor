package com.petg.blois.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.database")
public record DatabaseProperties(
        String jdbcUrlPattern,
        String host,
        String instance,
        String dbName,
        String appName,
        String username,
        String password,
        String schema
) {
}

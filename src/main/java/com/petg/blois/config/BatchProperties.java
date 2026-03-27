package com.petg.blois.config;

import com.petg.blois.data.SynchroKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch")
public record BatchProperties(
        SynchroKey key,
        int delay
) {

}

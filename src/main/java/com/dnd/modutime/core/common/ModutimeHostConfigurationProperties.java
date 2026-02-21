package com.dnd.modutime.core.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "modutime")
public record ModutimeHostConfigurationProperties(
        Host host
) {
    public record Host(
            String client,
            String server
    ) {
    }
}

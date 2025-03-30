package com.dnd.modutime.core.auth.oauth.facade;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
public record TokenConfigurationProperties(
        String accessTokenExpirationTime,
        String refreshTokenExpirationTime,
        String secret
) {
}

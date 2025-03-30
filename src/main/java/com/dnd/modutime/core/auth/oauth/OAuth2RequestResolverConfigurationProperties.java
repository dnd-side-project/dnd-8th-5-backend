package com.dnd.modutime.core.auth.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth2 리졸버 설정 속성
 */
@ConfigurationProperties(prefix = "clients.oauth2")
public record OAuth2RequestResolverConfigurationProperties(
        String clientMode,
        String authorizationRequestBaseUri
) {
    public static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";

    public String getAuthorizationRequestBaseUri() {
        return authorizationRequestBaseUri != null ? authorizationRequestBaseUri : DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
    }
}

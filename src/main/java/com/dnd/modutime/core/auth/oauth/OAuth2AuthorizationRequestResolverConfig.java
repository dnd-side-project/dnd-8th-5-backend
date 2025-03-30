package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.common.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;

/**
 * OAuth2 리졸버 설정 클래스입니다.
 *
 * 환경(local, dev, prod)에 따라 적절한 {@link OAuth2AuthorizationRequestResolver}를 생성합니다.
 *
 * local, dev 환경: Referer 헤더를 캡처하는 커스텀 리졸버 사용
 * prod 환경: 기본 리졸버 사용
 *
 * @see OAuth2HostCaptureAuthorizationRequestResolver
 * @see DefaultOAuth2AuthorizationRequestResolver
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2AuthorizationRequestResolverConfig {

    public static final String CLIENTS_OAUTH2_MODE = "clients.oauth2.client-mode";

    /**
     * 커스텀 OAuth2AuthorizationRequestResolver 구현체 사용 (local, dev 환경용)
     */
    @Configuration
    @ConditionalOnProperty(value = CLIENTS_OAUTH2_MODE, havingValue = Constants.CLIENT_MODE_STUB, matchIfMissing = true)
    @EnableConfigurationProperties({OAuth2RequestResolverConfigurationProperties.class})
    public static class RefererConfig {
        private final OAuth2RequestResolverConfigurationProperties properties;

        public RefererConfig(OAuth2RequestResolverConfigurationProperties properties) {
            this.properties = properties;
        }

        @Bean
        public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
            return new OAuth2HostCaptureAuthorizationRequestResolver(
                    clientRegistrationRepository,
                    properties.getAuthorizationRequestBaseUri()
            );
        }
    }

    /**
     * 기본 OAuth2AuthorizationRequestResolver 구현체 사용 (prod 환경용)
     */
    @Configuration
    @ConditionalOnProperty(value = CLIENTS_OAUTH2_MODE, havingValue = Constants.CLIENT_MODE_OAUTH2)
    @EnableConfigurationProperties({OAuth2RequestResolverConfigurationProperties.class})
    public static class RealConfig {
        private final OAuth2RequestResolverConfigurationProperties properties;

        public RealConfig(OAuth2RequestResolverConfigurationProperties properties) {
            this.properties = properties;
        }

        @Bean
        public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
            return new DefaultOAuth2AuthorizationRequestResolver(
                    clientRegistrationRepository,
                    properties.getAuthorizationRequestBaseUri()
            );
        }
    }
}

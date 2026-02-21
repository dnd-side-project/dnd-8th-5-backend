package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenService;
import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import com.dnd.modutime.core.common.Constants;
import com.dnd.modutime.core.common.ModutimeHostConfigurationProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.springframework.http.HttpHeaders.REFERER;

/**
 * OAuth2 인증 성공 핸들러 설정 클래스입니다.
 * <p>
 * 환경(local, dev, prod)에 따라 적절한 {@link AuthenticationSuccessHandler}를 생성합니다.
 * <p>
 * local, dev 환경: state 값에 포함된 Referer 헤더를 파싱한 후 해당 주소로 redirect
 * prod 환경: 명시적으로 지정된 프론트의 도메인 주소로 redirect
 *
 * @see OAuth2AuthenticationSuccessHandler
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2AuthenticationSuccessHandlerConfig {

    public static final String CLIENTS_OAUTH2_MODE = "clients.oauth2.client-mode";

    @Configuration
    @ConditionalOnProperty(value = CLIENTS_OAUTH2_MODE, havingValue = Constants.CLIENT_MODE_STUB, matchIfMissing = true)
    @EnableConfigurationProperties(ModutimeHostConfigurationProperties.class)
    public static class RefererConfig {

        private final OAuth2TokenProvider tokenProvider;
        private final OAuth2TokenService tokenService;
        private final TokenConfigurationProperties tokenConfigurationProperties;

        public RefererConfig(final OAuth2TokenProvider tokenProvider,
                             final OAuth2TokenService tokenService,
                             final TokenConfigurationProperties tokenConfigurationProperties) {
            this.tokenProvider = tokenProvider;
            this.tokenService = tokenService;
            this.tokenConfigurationProperties = tokenConfigurationProperties;
        }

        @Bean
        public AuthenticationSuccessHandler authenticationSuccessHandler() {
            return new OAuth2AuthenticationSuccessHandler(
                    this.tokenProvider,
                    this.tokenService,
                    this.tokenConfigurationProperties,
                    REFERER // redirect uri 가 Referer 인 경우 Referer 헤더 사용
            );
        }
    }

    @Configuration
    @ConditionalOnProperty(value = CLIENTS_OAUTH2_MODE, havingValue = Constants.CLIENT_MODE_OAUTH2)
    @EnableConfigurationProperties(ModutimeHostConfigurationProperties.class)
    public static class RealConfig {

        private final ModutimeHostConfigurationProperties properties;
        private final OAuth2TokenProvider tokenProvider;
        private final OAuth2TokenService tokenService;
        private final TokenConfigurationProperties tokenConfigurationProperties;

        public RealConfig(final ModutimeHostConfigurationProperties properties,
                          final OAuth2TokenProvider tokenProvider,
                          final OAuth2TokenService tokenService,
                          final TokenConfigurationProperties tokenConfigurationProperties) {
            this.properties = properties;
            this.tokenProvider = tokenProvider;
            this.tokenService = tokenService;
            this.tokenConfigurationProperties = tokenConfigurationProperties;
        }

        @Bean
        public AuthenticationSuccessHandler authenticationSuccessHandler() {
            return new OAuth2AuthenticationSuccessHandler(
                    this.tokenProvider,
                    this.tokenService,
                    this.tokenConfigurationProperties,
                    this.properties.host().client()
            );
        }
    }
}

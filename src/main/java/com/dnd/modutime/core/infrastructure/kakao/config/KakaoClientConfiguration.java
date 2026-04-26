package com.dnd.modutime.core.infrastructure.kakao.config;

import com.dnd.modutime.core.infrastructure.common.ClientConstants;
import com.dnd.modutime.core.infrastructure.common.CommonClientHttpRequestFactory;
import com.dnd.modutime.core.infrastructure.kakao.KakaoClient;
import com.dnd.modutime.core.infrastructure.kakao.KakaoRestClient;
import com.dnd.modutime.core.infrastructure.kakao.KakaoStubClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoClientConfiguration {

    public static final String CLIENT_MODE = "clients.kakao.client-mode";

    @Configuration
    @EnableConfigurationProperties(KakaoClientConfigurationProperties.class)
    @ConditionalOnProperty(value = CLIENT_MODE, havingValue = ClientConstants.CLIENT_MODE_REST)
    public static class RestClientConfig {

        private final KakaoClientConfigurationProperties configurationProperties;
        private final ObjectMapper objectMapper;

        public RestClientConfig(final KakaoClientConfigurationProperties configurationProperties,
                                final ObjectMapper objectMapper) {
            this.configurationProperties = configurationProperties;
            this.objectMapper = objectMapper;
        }

        @Bean
        public KakaoClient kakaoClient() {
            var clientProperties = this.configurationProperties.properties();

            var requestFactory = CommonClientHttpRequestFactory.create(
                    clientProperties.connectTimeout(),
                    clientProperties.readTimeout()
            );

            var restTemplate = new RestTemplateBuilder()
                    .rootUri(clientProperties.host())
                    .requestFactory(() -> requestFactory)
                    .additionalInterceptors(new KakaoRequestInterceptor(clientProperties.authenticationKey()))
                    .errorHandler(new KakaoResponseHandler(this.objectMapper))
                    .build();

            return new KakaoRestClient(restTemplate);
        }
    }

    @Configuration
    @EnableConfigurationProperties(KakaoClientConfigurationProperties.class)
    @ConditionalOnProperty(value = CLIENT_MODE, havingValue = ClientConstants.CLIENT_MODE_STUB, matchIfMissing = true)
    public static class StubClientConfig {

        @Bean
        public KakaoClient kakaoClient() {
            return new KakaoStubClient();
        }
    }
}

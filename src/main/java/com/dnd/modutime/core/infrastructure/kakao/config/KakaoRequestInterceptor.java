package com.dnd.modutime.core.infrastructure.kakao.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 모든 카카오 요청에 어드민 키 인증 헤더를 자동 주입한다.
 * Content-Type은 호출부 결정 (form-urlencoded, json 등이 다를 수 있음).
 */
public class KakaoRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final String AUTH_PREFIX = "KakaoAK ";

    private final String authorizationKey;

    public KakaoRequestInterceptor(final String authorizationKey) {
        this.authorizationKey = authorizationKey;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request,
                                        final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {
        if (StringUtils.hasText(this.authorizationKey)) {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, AUTH_PREFIX + this.authorizationKey);
        }
        return execution.execute(request, body);
    }
}

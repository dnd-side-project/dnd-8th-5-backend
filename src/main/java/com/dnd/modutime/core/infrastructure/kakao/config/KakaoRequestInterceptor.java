package com.dnd.modutime.core.infrastructure.kakao.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 카카오 요청에 어드민 키 인증 헤더를 자동 주입한다.
 * 호출부에서 이미 Authorization 헤더를 명시한 경우(예: 사용자 access token 으로 호출하는 /v2/user/me)에는
 * 덮어쓰지 않고 그대로 둔다.
 * Content-Type 은 호출부 결정 (form-urlencoded, json 등이 다를 수 있음).
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
        boolean alreadyAuthorized = request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
        if (!alreadyAuthorized && StringUtils.hasText(this.authorizationKey)) {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, AUTH_PREFIX + this.authorizationKey);
        }
        return execution.execute(request, body);
    }
}

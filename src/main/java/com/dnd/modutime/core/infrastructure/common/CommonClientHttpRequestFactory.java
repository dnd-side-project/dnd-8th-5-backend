package com.dnd.modutime.core.infrastructure.common;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

/**
 * 외부 클라이언트 공용 RequestFactory 생성기.
 * Spring Boot 2.7 / SimpleClientHttpRequestFactory 기반 — 추가 의존성 없음.
 * 더 정교한 풀링/재시도가 필요하면 HttpComponentsClientHttpRequestFactory로 교체할 수 있다.
 */
public final class CommonClientHttpRequestFactory {

    private CommonClientHttpRequestFactory() {
    }

    public static ClientHttpRequestFactory create(final Duration connectTimeout, final Duration readTimeout) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) connectTimeout.toMillis());
        factory.setReadTimeout((int) readTimeout.toMillis());
        return factory;
    }
}

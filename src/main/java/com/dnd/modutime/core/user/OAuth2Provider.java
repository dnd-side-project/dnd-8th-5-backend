package com.dnd.modutime.core.user;

import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum OAuth2Provider {

    KAKAO("kakao")
    ;

    private static final Map<String, OAuth2Provider> OAUTH2_PROVIDER_MAP = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(OAuth2Provider::getRegistrationId, Function.identity()));

    private final String registrationId;

    OAuth2Provider(final String registrationId) {
        this.registrationId = registrationId;
    }

    public static OAuth2Provider findByRegistrationId(final String registrationId) {
        OAuth2Provider provider = OAUTH2_PROVIDER_MAP.get(registrationId);
        if (provider == null) {
            throw new IllegalArgumentException(String.format("요청한 registrationId(%s)를 찾을 수 없습니다.", registrationId));
        }

        return provider;
    }

}

package com.dnd.modutime.core.auth.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAttributes(
    Map<String, Object> properties,
    Map<String, Object> kakao_account
) {
}

package com.dnd.modutime.core.infrastructure.kakao.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 에러 응답.
 *
 * <pre>
 * { "msg": "IllegalParamException", "code": -2 }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoErrorResponse(
        @JsonProperty("msg") String message,
        @JsonProperty("code") String code
) {
}

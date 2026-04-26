package com.dnd.modutime.core.infrastructure.kakao.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 카카오 연결 끊기 응답.
 *
 * <pre>
 * { "id": 123456 }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUnlinkResponse(Long id) {
}

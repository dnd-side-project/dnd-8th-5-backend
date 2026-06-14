package com.dnd.modutime.core.admin.controller.dto;

/**
 * 어드민 토큰 재발급 요청 바디.
 * 웹은 쿠키(refreshToken)로, 도구/네이티브는 JSON 바디로 refresh token 을 전달할 수 있다.
 */
public record AdminReissueTokenRequest(String refreshToken) {
}

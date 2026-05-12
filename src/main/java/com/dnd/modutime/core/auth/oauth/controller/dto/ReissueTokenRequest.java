package com.dnd.modutime.core.auth.oauth.controller.dto;

/**
 * 네이티브 앱이 재발급 요청 시 JSON 바디로 refresh token 을 전달하기 위한 DTO.
 * 웹 클라이언트는 쿠키(refreshToken)로 보낼 수 있으며 본 바디는 선택 사항이다.
 */
public record ReissueTokenRequest(String refreshToken) {
}

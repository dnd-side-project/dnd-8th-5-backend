package com.dnd.modutime.core.auth.oauth.controller.dto;

import javax.validation.constraints.NotBlank;

/**
 * 네이티브 앱 카카오 로그인 요청.
 *
 * @param kakaoAccessToken 카카오 SDK 가 발급한 사용자 access token (Bearer)
 * @param roomUuid         로그인 직후 진입할 방 UUID (선택; 응답에 echo)
 */
public record KakaoNativeLoginRequest(
        @NotBlank(message = "kakaoAccessToken 은 필수입니다.")
        String kakaoAccessToken,

        String roomUuid
) {
}

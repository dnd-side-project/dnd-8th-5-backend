package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.dto.JwtTokenResponse;
import com.dnd.modutime.core.user.User;

/**
 * 네이티브 카카오 로그인 처리 결과 — Facade 와 CommandHandler 간 전달용.
 * 컨트롤러 응답 DTO 와는 별도로 분리해 두어, 응답 포맷 변경이 CommandHandler 시그니처에 영향을 주지 않도록 한다.
 */
public record KakaoNativeLoginResult(JwtTokenResponse tokens, User user) {
}

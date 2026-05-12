package com.dnd.modutime.core.auth.oauth.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2SecurityConfig;
import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2LoginResponse;
import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2ReIssueTokenResponse;
import com.dnd.modutime.core.auth.oauth.controller.dto.ReissueTokenRequest;
import com.dnd.modutime.core.auth.oauth.facade.BadCredentialsException;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenService;
import com.dnd.modutime.core.common.ErrorCode;
import org.springframework.web.bind.annotation.*;

@RestController
public class OAuth2Controller {

    private final com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenService OAuth2TokenService;

    public OAuth2Controller(final OAuth2TokenService OAuth2TokenService) {
        this.OAuth2TokenService = OAuth2TokenService;
    }

    /**
     * 클라이언트 전달 문서용 컨트롤러
     * <p>
     * OAuth2 로그인 API입니다.
     * 현재 지원하는 OAuth2 Provider(registrationId)는 kakao입니다.
     * oauth2/authorization/{registrationId} 로 GET 요청을 보내면 OAuth2 로그인 페이지로 리다이렉트 됩니다.
     * OAuth2 인증 후 사용자를 리디렉션할 호스트 정보를 전달하기 위해, 요청 시 쿼리 파라미터로 host 값을 포함해야 합니다.
     * 이를 통해 애플리케이션은 인증이 완료된 후 사용자를 적절한 클라이언트 애플리케이션으로 리다이렉트할 수 있습니다.
     * OAuth2 로그인 과정은 다음과 같습니다.
     * > 1. 사용자가 /oauth2/authorization/{registrationId}로 GET 요청을 보내면, 해당 OAuth2 Provider의 로그인 페이지로 리다이렉트됩니다.
     * > 2. 사용자가 OAuth2 Provider에서 인증을 완료하면, 해당 Provider는 인가 코드(Authorization Code)를 애플리케이션에 전달합니다.
     * > 3. 애플리케이션은 받은 인가 코드를 사용하여 OAuth2 Provider로부터 액세스 토큰(Access Token)을 요청합니다.
     * > 4. OAuth2 Provider는 액세스 토큰을 애플리케이션에 반환합니다.
     * > 5. 애플리케이션은 받은 액세스 토큰을 사용하여, 애플리케이션에서 사용할 자체 액세스 토큰과 리프레시 토큰을 생성합니다.
     * > 6. 생성된 액세스 토큰은 클라이언트와의 인증을 위해 사용되며, 리프레시 토큰은 액세스 토큰 갱신 시 사용됩니다.
     *
     * @see OAuth2SecurityConfig
     */
    @GetMapping("/oauth2/authorization/{registrationId}")
    public OAuth2LoginResponse oAuth2Login(@RequestParam(value = "roomUuid", required = false) String roomUuid) {
        return null;
    }

    /**
     * 사용자의 리프레시 토큰을 이용하여 새로운 액세스 토큰을 생성합니다.
     *
     * <p>두 가지 방식으로 refreshToken 을 전달할 수 있습니다.</p>
     * <ol>
     *   <li>웹 클라이언트: 'refreshToken' 쿠키 (기존 동작)</li>
     *   <li>네이티브 앱: JSON 바디 {@code {"refreshToken": "..."}}</li>
     * </ol>
     *
     * <p>두 방식 모두 제공된 경우 JSON 바디가 우선합니다 (앱이 명시적으로 보낸 값을 신뢰).</p>
     */
    @PostMapping("/oauth2/reissue-token")
    public OAuth2ReIssueTokenResponse oAuth2ReIssueToken(
            @CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) ReissueTokenRequest body
    ) {
        String refreshToken = resolveRefreshToken(cookieToken, body);
        if (refreshToken == null) {
            throw new BadCredentialsException("refreshToken 이 존재하지 않습니다.", ErrorCode.MISSING_COOKIE);
        }

        return this.OAuth2TokenService.createOAuth2AccessTokenByRefreshToken(refreshToken);
    }

    private String resolveRefreshToken(final String cookieToken, final ReissueTokenRequest body) {
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }
        return cookieToken;
    }

    /**
     * 클라이언트 전달 문서용 컨트롤러
     * <p>
     * OAuth2 로그아웃 API입니다.
     * 실제 처리는 컨트롤러가 아닌 {@link com.dnd.modutime.core.auth.oauth.OAuth2LogoutFilter} 와
     * Spring Security의 LogoutFilter 체인에서 수행되므로 이 메서드 본문은 호출되지 않습니다.
     * 본 매핑은 REST Docs/OpenAPI 명세에 엔드포인트를 노출하기 위한 목적으로만 존재합니다.
     * <p>
     * 요청 시 Authorization 헤더에 'Bearer {accessToken}' 형식으로 액세스 토큰을 전달해야 합니다.
     * 처리 결과는 다음과 같습니다.
     * > 1. DB에 저장된 refreshToken 만료 처리
     * > 2. 서버 캐시(OAuth2UserCache)에서 사용자 무효화
     * > 3. 클라이언트의 refreshToken / refreshTokenExpireTime 쿠키 즉시 만료
     * > 4. 세션 무효화 및 JSESSIONID 쿠키 삭제
     *
     * @see com.dnd.modutime.core.auth.oauth.OAuth2LogoutFilter
     * @see com.dnd.modutime.core.auth.oauth.OAuth2LogoutSuccessHandler
     */
    @PostMapping("/oauth2/logout")
    public void oAuth2Logout() {
        // no-op: 실제 처리는 필터 체인에서 수행됩니다.
    }
}

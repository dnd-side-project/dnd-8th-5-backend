package com.dnd.modutime.core.auth.oauth.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2SecurityConfig;
import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2LoginResponse;
import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2ReIssueTokenResponse;
import com.dnd.modutime.core.auth.oauth.facade.BadCredentialsException;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenService;
import com.dnd.modutime.core.common.ErrorCode;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public OAuth2LoginResponse oAuth2Login() {
        return null;
    }

    /**
     * 사용자의 리프레시 토큰을 이용하여 새로운 액세스 토큰을 생성합니다.
     * 클라이언트는 'refreshToken' 이라는 이름의 쿠키에 리프레시 토큰을 포함하여 요청을 보내야 합니다.
     * 서버에서 요청을 검증한 후 새로운 액세스 토큰을 발급합니다.
     */
    @PostMapping("/oauth2/reissue-token")
    public OAuth2ReIssueTokenResponse oAuth2ReIssueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new BadCredentialsException("refreshToken 쿠키가 존재하지 않습니다.", ErrorCode.MISSING_COOKIE);
        }

        return this.OAuth2TokenService.createOAuth2AccessTokenByRefreshToken(refreshToken);
    }
}

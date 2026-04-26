package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {

    private final TokenConfigurationProperties tokenConfigurationProperties;

    public OAuth2LogoutSuccessHandler(final TokenConfigurationProperties tokenConfigurationProperties) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
    }

    /**
     * 로그아웃 성공 시 처리.
     * 로그인 시 발급한 refreshToken 관련 쿠키를 maxAge=0으로 즉시 만료시키고 200 응답을 반환한다.
     * 쿠키 옵션은 {@link OAuth2AuthenticationSuccessHandler}의 발급 옵션과 일치해야 브라우저가 동일 쿠키로 인식해 제거한다.
     */
    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        expireCookie(response, "refreshToken");
        expireCookie(response, "refreshTokenExpireTime");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void expireCookie(final HttpServletResponse response, final String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(tokenConfigurationProperties.secureCookie())
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}

package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.dto.JwtTokenResponse;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenService;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2TokenProvider oAuth2TokenProvider;
    private final OAuth2TokenService oAuth2TokenService;
    private String clientRedirectUri;

    private final static int REFRESH_TOKEN_EXPIRE_TIME = 1209600; // 2주

    public OAuth2AuthenticationSuccessHandler(final OAuth2TokenProvider tokenProvider,
                                              final OAuth2TokenService tokenService,
                                              final String clientRedirectUri) {
        this.oAuth2TokenProvider = tokenProvider;
        this.oAuth2TokenService = tokenService;
        this.clientRedirectUri = clientRedirectUri;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = authentication.getName().split(":")[1];
        OAuth2Provider provider = oAuth2User.getProvider();

        JwtTokenResponse oAuth2JwtTokenResponse = this.oAuth2TokenProvider.createOAuth2JwtTokenResponse(email, provider);

        this.oAuth2TokenService.saveOrUpdateOAuth2RefreshToken(email, provider, oAuth2JwtTokenResponse);

        // access token은 쿼리 파라미터로 전송, refresh token은 쿠키로 전송
        addCookie(response, "refreshToken", oAuth2JwtTokenResponse.refreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        addCookie(response, "refreshTokenExpireTime", DateTimeUtils.toISO8601(oAuth2JwtTokenResponse.refreshTokenExpireTime()), REFRESH_TOKEN_EXPIRE_TIME);

        clearAuthenticationAttributes(request);

        String accessToken = URLEncoder.encode(oAuth2JwtTokenResponse.accessToken(), StandardCharsets.UTF_8);
        String accessTokenExpireTime = URLEncoder.encode(
                DateTimeUtils.toISO8601(oAuth2JwtTokenResponse.accessTokenExpireTime()),
                StandardCharsets.UTF_8
        );

        if (HttpHeaders.REFERER.equals(this.clientRedirectUri)) {
            String state = request.getParameter("state");
            clientRedirectUri = state.split("\\|")[1];
        }

        String endpoint = "auth";
        URI redirectUrl = URI.create(clientRedirectUri).resolve(endpoint);
        log.info("redirectUrl: {}", redirectUrl);

        URI redirectUriWithParams = UriComponentsBuilder.fromUriString(redirectUrl.toString())
                .queryParam("access_token", accessToken)
                .queryParam("access_token_expiration_time", accessTokenExpireTime)
                .build(true)
                .toUri();

        response.sendRedirect(redirectUriWithParams.toString());
    }

    /**\
     * 이 메서드는 인증 과정 중에 세션에 저장된 임시 데이터를 제거하기 위해 사용됩니다.
     * 임시 데이터는 주로 인증 실패 시에 저장된 정보로, 예를 들어 사용자가 잘못된 자격 증명을 입력했을 때 발생한 예외 정보 등이 포함됩니다.
     */
    protected final void clearAuthenticationAttributes(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);  // XSS 공격 방어
        // cookie.setSecure(true); // HTTPS 연결에서만 전송 TODO :: 운영서버 배포 후 쿠키 설정 점검
        // cookie.setDomain(); // 도메인 설정
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}

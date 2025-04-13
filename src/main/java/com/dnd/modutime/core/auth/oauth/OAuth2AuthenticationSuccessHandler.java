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

import static com.dnd.modutime.core.auth.oauth.OAuth2Constants.REDIRECT_END_POINT;

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
        var oAuth2User = (OAuth2User) authentication.getPrincipal();
        var email = authentication.getName().split(":")[1];
        var provider = oAuth2User.getProvider();

        var tokenResponse = createAndSaveTokens(email, provider);

        addRefreshTokenCookies(response, tokenResponse);

        clearAuthenticationAttributes(request);

        var redirectUrl = buildRedirectUrl(request, tokenResponse);
        response.sendRedirect(redirectUrl);
    }

    private JwtTokenResponse createAndSaveTokens(String email, OAuth2Provider provider) {
        var tokenResponse = this.oAuth2TokenProvider.createOAuth2JwtTokenResponse(email, provider);
        this.oAuth2TokenService.saveOrUpdateOAuth2RefreshToken(email, provider, tokenResponse);
        return tokenResponse;
    }

    private void addRefreshTokenCookies(HttpServletResponse response, JwtTokenResponse tokenResponse) {
        addCookie(response, "refreshToken", tokenResponse.refreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        addCookie(
                response,
                "refreshTokenExpireTime",
                DateTimeUtils.toISO8601(tokenResponse.refreshTokenExpireTime()),
                REFRESH_TOKEN_EXPIRE_TIME
        );
    }

    private String buildRedirectUrl(HttpServletRequest request, JwtTokenResponse tokenResponse) {
        var accessToken = URLEncoder.encode(tokenResponse.accessToken(), StandardCharsets.UTF_8);
        var accessTokenExpireTime = URLEncoder.encode(
                DateTimeUtils.toISO8601(tokenResponse.accessTokenExpireTime()),
                StandardCharsets.UTF_8
        );

        var stateParts = extractStateParts(request);
        updateRedirectUriIfNeeded(stateParts);
        var roomUuid = extractRoomUuid(stateParts);

        var baseRedirectUrl = URI.create(clientRedirectUri).resolve(REDIRECT_END_POINT);
        log.info("redirectUrl: {}", baseRedirectUrl);

        return buildFinalRedirectUrl(baseRedirectUrl, accessToken, accessTokenExpireTime, roomUuid);
    }

    private String[] extractStateParts(HttpServletRequest request) {
        var state = request.getParameter("state");
        return state.split("\\|");
    }

    private void updateRedirectUriIfNeeded(String[] stateParts) {
        if (HttpHeaders.REFERER.equals(this.clientRedirectUri) && stateParts.length > 1) {
            clientRedirectUri = stateParts[1];
        }
    }

    private String extractRoomUuid(String[] stateParts) {
        if (stateParts.length > 2) {
            return stateParts[2];
        }
        return null;
    }

    private String buildFinalRedirectUrl(URI baseUrl, String accessToken, String accessTokenExpireTime, String roomUuid) {
        var uriBuilder = UriComponentsBuilder.fromUriString(baseUrl.toString())
                .queryParam("access_token", accessToken)
                .queryParam("access_token_expiration_time", accessTokenExpireTime);

        if (roomUuid != null && !roomUuid.isEmpty()) {
            uriBuilder.queryParam("room_uuid", roomUuid);
        }

        return uriBuilder.build(true).toUri().toString();
    }

    protected final void clearAuthenticationAttributes(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);  // XSS 공격 방어
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}

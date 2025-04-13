package com.dnd.modutime.core.auth.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;

import static com.dnd.modutime.core.auth.oauth.OAuth2Constants.TRAILING_SLASH;
import static org.springframework.http.HttpHeaders.REFERER;

/**
 * 클라이언트에서 OAuth2 인증 요청 시 Referer Header를 추출하여 state 값에 추가합니다.
 * {@link OAuth2AuthenticationSuccessHandler} 에서 state 값을 파싱하여 Redirect URL을 생성할 때 사용합니다.
 * <p>
 * local, dev 환경에서 사용됩니다.
 *
 * @see OAuth2AuthorizationRequestResolverConfig
 */
@Slf4j
public class OAuth2HostCaptureAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public OAuth2HostCaptureAuthorizationRequestResolver(ClientRegistrationRepository repo, String authorizationRequestBaseUri) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        var authorizationRequest = defaultResolver.resolve(request);
        return customizeAuthorizationRequest(authorizationRequest, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        var authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(authorizationRequest, request);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest == null) {
            return null;
        }

        var enhancedState = buildEnhancedState(authorizationRequest.getState(), request);

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .state(enhancedState)
                .build();
    }

    private String buildEnhancedState(String originalState, HttpServletRequest request) {
        var referer = normalizeReferer(request.getHeader(REFERER));
        var roomUuid = request.getParameter("room-uuid");

        if (roomUuid != null && !roomUuid.isEmpty()) {
            return String.format("%s|%s|%s", originalState, referer, roomUuid);
        }

        return String.format("%s|%s", originalState, referer);
    }

    /**
     * Referer URL을 정규화하여 trailing slash(/)를 제거합니다.
     *
     * @param referer 원본 Referer URL
     * @return 정규화된 Referer URL
     */
    private String normalizeReferer(String referer) {
        if (referer == null) {
            return null;
        }

        return referer.endsWith(TRAILING_SLASH)
                ? referer.substring(0, referer.length() - 1)
                : referer;
    }
}

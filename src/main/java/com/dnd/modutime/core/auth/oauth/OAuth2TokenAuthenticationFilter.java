package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.exception.OAuth2AuthenticationException;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import com.dnd.modutime.core.auth.oauth.validation.OAuth2AuthorizationHeaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.dnd.modutime.core.common.Constants.AUTHORIZATION;
import static com.dnd.modutime.core.common.Constants.TOKEN_PREFIX_SEPARATOR;

@Slf4j
public class OAuth2TokenAuthenticationFilter extends OncePerRequestFilter {

    private final OAuth2TokenProvider oAuth2TokenProvider;
    private final RequestMatcher permitAllMatchers;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public OAuth2TokenAuthenticationFilter(final OAuth2TokenProvider oAuth2TokenProvider,
                                           final RequestMatcher permitAllMatchers,
                                           final AuthenticationEntryPoint authenticationEntryPoint) {
        this.oAuth2TokenProvider = oAuth2TokenProvider;
        this.permitAllMatchers = permitAllMatchers;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        try {
            String oAuth2AccessToken = resolveToken(request);

            if (this.oAuth2TokenProvider.validateOAuth2Token(oAuth2AccessToken)) {
                setAuthentication(oAuth2AccessToken);
            }
        } catch (OAuth2AuthenticationException e) {
            this.authenticationEntryPoint.commence(request, response, e);
            return; // 인증 실패 시 필터 체인 종료
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        OAuth2AuthorizationHeaderUtils.validateAuthorization(authorizationHeader);

        return authorizationHeader.split(TOKEN_PREFIX_SEPARATOR)[1];
    }

    private void setAuthentication(final String oAuth2AccessToken) {
        Authentication authentication = this.oAuth2TokenProvider.getAuthentication(oAuth2AccessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        return this.permitAllMatchers.matches(request);
    }
}

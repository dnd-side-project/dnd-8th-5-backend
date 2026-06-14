package com.dnd.modutime.core.admin.security;

import com.dnd.modutime.core.admin.application.AdminTokenProvider;
import com.dnd.modutime.core.auth.oauth.validation.OAuth2AuthorizationHeaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.dnd.modutime.core.common.Constants.AUTHORIZATION;
import static com.dnd.modutime.core.common.Constants.TOKEN_PREFIX_SEPARATOR;

/**
 * /admin/** 요청의 Authorization Bearer 토큰을 어드민 토큰으로 검증하고 인증 컨텍스트를 설정한다.
 * permit 대상(로그인/재발급)은 {@link #shouldNotFilter(HttpServletRequest)} 로 건너뛴다.
 */
@Slf4j
public class AdminTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final AdminTokenProvider adminTokenProvider;
    private final RequestMatcher permitAllMatchers;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public AdminTokenAuthenticationFilter(final AdminTokenProvider adminTokenProvider,
                                          final RequestMatcher permitAllMatchers,
                                          final AuthenticationEntryPoint authenticationEntryPoint) {
        this.adminTokenProvider = adminTokenProvider;
        this.permitAllMatchers = permitAllMatchers;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = resolveToken(request);
            if (this.adminTokenProvider.validateAdminToken(accessToken)) {
                setAuthentication(accessToken);
            }
        } catch (AuthenticationException e) {
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

    private void setAuthentication(final String accessToken) {
        String username = this.adminTokenProvider.getUsername(accessToken);
        var authentication = new PreAuthenticatedAuthenticationToken(
                username, accessToken, List.of(new SimpleGrantedAuthority(ROLE_ADMIN)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return this.permitAllMatchers.matches(request);
    }
}

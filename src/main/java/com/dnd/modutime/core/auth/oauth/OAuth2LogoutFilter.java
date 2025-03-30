package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.exception.OAuth2AuthenticationException;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2LogoutService;
import com.dnd.modutime.core.auth.oauth.validation.OAuth2AuthorizationHeaderUtils;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.dnd.modutime.core.common.Constants.TOKEN_PREFIX_SEPARATOR;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class OAuth2LogoutFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final OAuth2LogoutService oAuth2LogoutService;

    public OAuth2LogoutFilter(final ObjectMapper objectMapper, final OAuth2LogoutService oAuth2LogoutService) {
        this.objectMapper = objectMapper;
        this.oAuth2LogoutService = oAuth2LogoutService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (isLogoutRequest(request)) {
            try {
                String authorizationHeader = request.getHeader(AUTHORIZATION);
                OAuth2AuthorizationHeaderUtils.validateAuthorization(authorizationHeader);
                String accessToken = authorizationHeader.split(TOKEN_PREFIX_SEPARATOR)[1];

                this.oAuth2LogoutService.logout(accessToken);
            } catch (OAuth2AuthenticationException e) {
                sendErrorResponse(response, e.getErrorCode(), e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLogoutRequest(final HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/oauth2/logout");
    }

    private void sendErrorResponse(final HttpServletResponse response, final ErrorCode errorCode, final String errorMessage, final int status) {
        try {
            var errorResponse = new ErrorResponse(errorCode.getCode(), errorMessage, status);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(status);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.error("에러 응답을 전송하는 데 실패했습니다.");
            throw new IllegalStateException("에러 응답을 전송하는 데 실패했습니다.", e);
        }
    }
}

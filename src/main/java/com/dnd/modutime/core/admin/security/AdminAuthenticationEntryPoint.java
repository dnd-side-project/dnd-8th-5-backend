package com.dnd.modutime.core.admin.security;

import com.dnd.modutime.core.admin.exception.AdminTokenException;
import com.dnd.modutime.core.auth.oauth.SecurityErrorCodeResponseHandler;
import com.dnd.modutime.core.auth.oauth.exception.OAuth2AuthenticationException;
import com.dnd.modutime.core.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 어드민 엔드포인트(/admin/**) 에서 인증 실패 시 401 응답을 {@code ErrorResponse} 포맷으로 직렬화한다.
 */
public class AdminAuthenticationEntryPoint extends SecurityErrorCodeResponseHandler implements AuthenticationEntryPoint {

    public AdminAuthenticationEntryPoint(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        var status = HttpServletResponse.SC_UNAUTHORIZED;
        ErrorCode errorCode = determineErrorCode(authException);
        handle(request, response, errorCode.getCode(), authException.getMessage(), status);
    }

    private ErrorCode determineErrorCode(final AuthenticationException authException) {
        if (authException instanceof AdminTokenException) {
            return ((AdminTokenException) authException).getErrorCode();
        }
        if (authException instanceof OAuth2AuthenticationException) {
            return ((OAuth2AuthenticationException) authException).getErrorCode();
        }
        return ErrorCode.MT401;
    }
}

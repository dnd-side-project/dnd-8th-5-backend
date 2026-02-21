package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2AuthenticationEntryPoint extends SecurityErrorCodeResponseHandler implements AuthenticationEntryPoint {

    public OAuth2AuthenticationEntryPoint(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException, ServletException {
        var status = HttpServletResponse.SC_UNAUTHORIZED;

        ErrorCode errorCode = OAuth2AuthenticationErrorCodeTranslator.determineErrorCode(authException);

        handle(request, response, errorCode.getCode(), authException.getMessage(), status);
    }
}

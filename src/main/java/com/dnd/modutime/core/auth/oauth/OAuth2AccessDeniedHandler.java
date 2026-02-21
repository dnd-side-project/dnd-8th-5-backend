package com.dnd.modutime.core.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2AccessDeniedHandler extends SecurityErrorCodeResponseHandler implements AccessDeniedHandler {

    public OAuth2AccessDeniedHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var status = HttpServletResponse.SC_FORBIDDEN;

        var errorCode = OAuth2AuthorizationErrorCodeTranslator.determineErrorCode(accessDeniedException);

        handle(request, response, errorCode.getCode(), accessDeniedException.getMessage(), status);
    }
}

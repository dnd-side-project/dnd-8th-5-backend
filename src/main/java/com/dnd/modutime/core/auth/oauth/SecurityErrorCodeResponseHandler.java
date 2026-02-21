package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SecurityErrorCodeResponseHandler {

    private final ObjectMapper objectMapper;

    public SecurityErrorCodeResponseHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // 공통 응답 처리 메소드
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       String errorCode, String errorMessage, int status) throws IOException {

        var errorResponse = new ErrorResponse(errorCode, errorMessage, status);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

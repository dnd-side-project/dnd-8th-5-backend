package com.dnd.modutime.core.auth.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {

    /**
     * 로그아웃 성공 시 처리
     * 현재는 클라이언트에게 200 상태 코드를 전달합니다.
     */
    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

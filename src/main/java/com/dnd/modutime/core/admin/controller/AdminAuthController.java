package com.dnd.modutime.core.admin.controller;

import com.dnd.modutime.core.admin.application.AdminAuthService;
import com.dnd.modutime.core.admin.application.request.AdminLoginRequest;
import com.dnd.modutime.core.admin.application.response.AdminLoginResponse;
import com.dnd.modutime.core.admin.application.response.AdminReissueTokenResponse;
import com.dnd.modutime.core.admin.controller.dto.AdminReissueTokenRequest;
import com.dnd.modutime.core.admin.exception.AdminAuthException;
import com.dnd.modutime.core.common.ErrorCode;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(final AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    /**
     * 어드민 로그인. username + 비밀번호를 검증하고 access/refresh JWT 를 JSON 으로 발급한다.
     */
    @PostMapping("/admin/login")
    public AdminLoginResponse login(@RequestBody @Valid AdminLoginRequest request) {
        return adminAuthService.login(request.username(), request.password());
    }

    /**
     * 어드민 토큰 재발급. refresh token 을 쿠키 또는 JSON 바디로 받아 새 access token 을 발급한다.
     * 둘 다 제공되면 바디가 우선한다.
     */
    @PostMapping("/admin/reissue-token")
    public AdminReissueTokenResponse reissue(
            @CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) AdminReissueTokenRequest body
    ) {
        String refreshToken = resolveRefreshToken(cookieToken, body);
        if (refreshToken == null) {
            throw new AdminAuthException("refreshToken 이 존재하지 않습니다.", ErrorCode.MISSING_COOKIE);
        }
        return adminAuthService.reissue(refreshToken);
    }

    private String resolveRefreshToken(final String cookieToken, final AdminReissueTokenRequest body) {
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }
        return cookieToken;
    }
}

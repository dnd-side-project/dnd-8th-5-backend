package com.dnd.modutime.core.admin.application;

import com.dnd.modutime.core.admin.application.response.AdminLoginResponse;
import com.dnd.modutime.core.admin.application.response.AdminReissueTokenResponse;
import com.dnd.modutime.core.admin.domain.Admin;
import com.dnd.modutime.core.admin.exception.AdminAuthException;
import com.dnd.modutime.core.admin.repository.AdminRepository;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.util.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final AdminTokenProvider adminTokenProvider;

    public AdminAuthService(final AdminRepository adminRepository,
                            final AdminTokenProvider adminTokenProvider) {
        this.adminRepository = adminRepository;
        this.adminTokenProvider = adminTokenProvider;
    }

    /**
     * username + 평문 비밀번호로 어드민을 인증하고 access/refresh JWT 를 발급한다.
     * 발급된 refresh token 은 어드민 엔티티에 저장되어 이후 재발급 검증에 사용된다.
     */
    @Transactional
    public AdminLoginResponse login(final String username, final String rawPassword) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new AdminAuthException("아이디 또는 비밀번호가 올바르지 않습니다.", ErrorCode.BAD_CREDENTIALS));

        if (!admin.matchesPassword(rawPassword)) {
            throw new AdminAuthException("아이디 또는 비밀번호가 올바르지 않습니다.", ErrorCode.BAD_CREDENTIALS);
        }

        var accessTokenExpireTime = adminTokenProvider.createAccessTokenExpireTime();
        var refreshTokenExpireTime = adminTokenProvider.createRefreshTokenExpireTime();
        var accessToken = adminTokenProvider.createAccessToken(admin.getUsername());
        var refreshToken = adminTokenProvider.createRefreshToken(admin.getUsername());

        admin.updateRefreshToken(refreshToken, DateTimeUtils.convertDateToLocalDateTime(refreshTokenExpireTime));

        return new AdminLoginResponse(
                accessToken,
                DateTimeUtils.convertDateToLocalDateTime(accessTokenExpireTime),
                refreshToken,
                DateTimeUtils.convertDateToLocalDateTime(refreshTokenExpireTime)
        );
    }

    /**
     * 저장된 refresh token 으로 새로운 access token 을 발급한다. (refresh token 회전 없음)
     */
    @Transactional
    public AdminReissueTokenResponse reissue(final String refreshToken) {
        Admin admin = adminRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AdminAuthException("유효한 Refresh token이 아닙니다.", ErrorCode.INVALID_TOKEN));

        if (admin.isRefreshTokenExpired()) {
            throw new AdminAuthException("Refresh token이 만료되었습니다.", ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        var accessTokenExpireTime = adminTokenProvider.createAccessTokenExpireTime();
        var accessToken = adminTokenProvider.createAccessToken(admin.getUsername());

        return new AdminReissueTokenResponse(
                accessToken,
                DateTimeUtils.convertDateToLocalDateTime(accessTokenExpireTime)
        );
    }
}

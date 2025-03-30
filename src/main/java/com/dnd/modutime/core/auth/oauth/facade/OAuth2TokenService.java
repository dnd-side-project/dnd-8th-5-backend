package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2ReIssueTokenResponse;
import com.dnd.modutime.core.auth.oauth.dto.JwtTokenResponse;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.util.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2TokenService {

    private final OAuth2TokenProvider oAuth2TokenProvider;
    private final UserRepository userRepository;

    public OAuth2TokenService(final OAuth2TokenProvider oAuth2TokenProvider,
                              final UserRepository userRepository
    ) {
        this.oAuth2TokenProvider = oAuth2TokenProvider;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveOrUpdateOAuth2RefreshToken(final String email, final OAuth2Provider provider, final JwtTokenResponse oAuth2JwtTokenResponse) {
        var user = this.userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(() -> new BadCredentialsException("인증 정보가 유효하지 않습니다.", ErrorCode.BAD_CREDENTIALS));

        user.updateRefreshToken(oAuth2JwtTokenResponse.refreshToken(),
                DateTimeUtils.convertDateToLocalDateTime(oAuth2JwtTokenResponse.refreshTokenExpireTime()));
    }

    @Transactional
    public OAuth2ReIssueTokenResponse createOAuth2AccessTokenByRefreshToken(final String refreshToken) {
        var user = this.userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("유효한 Refresh token이 아닙니다.", ErrorCode.INVALID_TOKEN));

        if (user.isRefreshTokenExpired()) {
            throw new InvalidTokenException("Refresh token이 만료되었습니다.", ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        var oAuth2AccessTokenExpirationTime = this.oAuth2TokenProvider.createAccessTokenExpireTime();
        var oAuth2AccessToken = this.oAuth2TokenProvider.createOAuth2AccessToken(user.getEmail(), user.getProvider());

        return new OAuth2ReIssueTokenResponse(oAuth2AccessToken, DateTimeUtils.convertDateToLocalDateTime(oAuth2AccessTokenExpirationTime));
    }
}

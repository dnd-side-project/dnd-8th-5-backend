package com.dnd.modutime.core.auth.oauth.dto;

import java.util.Date;

public record JwtTokenResponse(
        String accessToken,
        Date accessTokenExpireTime,
        String refreshToken,
        Date refreshTokenExpireTime
) {
}

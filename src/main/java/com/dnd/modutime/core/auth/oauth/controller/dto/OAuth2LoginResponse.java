package com.dnd.modutime.core.auth.oauth.controller.dto;

import java.time.LocalDateTime;

public record OAuth2LoginResponse(

        String accessToken,
        LocalDateTime accessTokenExpireTime,
        String refreshToken,
        LocalDateTime refreshTokenExpireTime
) {
}

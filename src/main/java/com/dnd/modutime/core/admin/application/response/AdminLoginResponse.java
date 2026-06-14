package com.dnd.modutime.core.admin.application.response;

import java.time.LocalDateTime;

public record AdminLoginResponse(
        String accessToken,
        LocalDateTime accessTokenExpirationTime,
        String refreshToken,
        LocalDateTime refreshTokenExpirationTime
) {
}

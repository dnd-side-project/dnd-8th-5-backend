package com.dnd.modutime.core.admin.application.response;

import java.time.LocalDateTime;

public record AdminReissueTokenResponse(
        String accessToken,
        LocalDateTime accessTokenExpirationTime
) {
}

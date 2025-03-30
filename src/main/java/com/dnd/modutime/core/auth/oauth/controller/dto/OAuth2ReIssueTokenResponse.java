package com.dnd.modutime.core.auth.oauth.controller.dto;

import java.time.LocalDateTime;

public record OAuth2ReIssueTokenResponse(
        String accessToken,
        LocalDateTime accessTokenExpirationTime
) {
}

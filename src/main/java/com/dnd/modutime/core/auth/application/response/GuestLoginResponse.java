package com.dnd.modutime.core.auth.application.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuestLoginResponse {

    private String accessToken;
    private LocalDateTime accessTokenExpireTime;
}

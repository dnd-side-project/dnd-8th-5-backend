package com.dnd.modutime.core.auth.oauth.controller.dto;

import com.dnd.modutime.core.auth.oauth.dto.JwtTokenResponse;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.util.DateTimeUtils;

import java.time.LocalDateTime;

/**
 * 네이티브 앱 카카오 로그인 성공 응답.
 *
 * <p>웹 흐름과 달리 토큰은 쿠키가 아닌 JSON 바디로 전달된다. 앱은 refreshToken 을
 * Keychain/EncryptedSharedPreferences 에 직접 저장하고, 이후 /oauth2/reissue-token 호출 시
 * JSON 바디로 전달한다.</p>
 */
public record KakaoNativeLoginResponse(
        String accessToken,
        LocalDateTime accessTokenExpirationTime,
        String refreshToken,
        LocalDateTime refreshTokenExpirationTime,
        UserSummary user,
        String roomUuid
) {
    public static KakaoNativeLoginResponse of(final JwtTokenResponse tokens, final User user, final String roomUuid) {
        return new KakaoNativeLoginResponse(
                tokens.accessToken(),
                DateTimeUtils.convertDateToLocalDateTime(tokens.accessTokenExpireTime()),
                tokens.refreshToken(),
                DateTimeUtils.convertDateToLocalDateTime(tokens.refreshTokenExpireTime()),
                UserSummary.from(user),
                roomUuid
        );
    }
}

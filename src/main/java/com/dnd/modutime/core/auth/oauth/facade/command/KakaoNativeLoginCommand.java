package com.dnd.modutime.core.auth.oauth.facade.command;

import com.dnd.modutime.core.auth.oauth.OAuth2UserDetails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

/**
 * 네이티브 카카오 로그인 커맨드.
 *
 * <p>{@link com.dnd.modutime.core.auth.oauth.facade.KakaoNativeAuthFacade} 가 외부 카카오 호출
 * 결과를 {@link #bindUserDetails} 로 채워 {@link com.dnd.modutime.core.auth.oauth.facade.KakaoNativeLoginCommandHandler}
 * 에 위임한다. UserWithdrawCommand 와 동일한 정적 팩토리 스타일.</p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoNativeLoginCommand {

    private String kakaoAccessToken;
    private String roomUuid;

    private OAuth2UserDetails userDetails;
    private Map<String, Object> rawAttributes;

    public static KakaoNativeLoginCommand of(final String kakaoAccessToken, final String roomUuid) {
        var command = new KakaoNativeLoginCommand();
        command.kakaoAccessToken = Objects.requireNonNull(kakaoAccessToken, "kakaoAccessToken");
        command.roomUuid = roomUuid;
        return command;
    }

    /**
     * Facade 가 카카오 호출 결과를 채워 CommandHandler 에 넘기기 직전에 호출한다.
     */
    public KakaoNativeLoginCommand bindUserDetails(final OAuth2UserDetails userDetails,
                                                    final Map<String, Object> rawAttributes) {
        this.userDetails = Objects.requireNonNull(userDetails, "userDetails");
        this.rawAttributes = Objects.requireNonNull(rawAttributes, "rawAttributes");
        return this;
    }
}

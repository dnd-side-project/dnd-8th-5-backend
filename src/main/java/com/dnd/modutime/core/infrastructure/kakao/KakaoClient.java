package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.infrastructure.kakao.config.dto.KakaoUnlinkResponse;

import java.util.Map;

public interface KakaoClient {

    KakaoUnlinkResponse unlink(String targetId, String targetIdType);

    default KakaoUnlinkResponse unlinkByUserId(final String oauthId) {
        return unlink(oauthId, "user_id");
    }

    /**
     * 사용자 access token 으로 카카오 사용자 정보를 조회한다 (POST /v2/user/me).
     *
     * <p>반환되는 attribute 맵은 Spring Security {@code DefaultOAuth2UserService} 가 사용하는
     * 구조와 동일하므로 {@link com.dnd.modutime.core.auth.oauth.OAuth2UserDetails#of} 에
     * 그대로 넘길 수 있다.</p>
     *
     * @param userAccessToken 카카오 SDK 가 발급한 사용자 access token
     * @return 카카오 사용자 정보 attribute 맵
     */
    Map<String, Object> getUserInfo(String userAccessToken);
}

package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.auth.oauth.exception.InvalidKakaoAccessTokenException;
import com.dnd.modutime.core.infrastructure.kakao.config.dto.KakaoUnlinkResponse;

import java.util.HashMap;
import java.util.Map;

public class KakaoStubClient implements KakaoClient {

    static final String INVALID_TOKEN_MARKER = "invalid";
    static final String NO_EMAIL_TOKEN_MARKER = "no-email";

    @Override
    public KakaoUnlinkResponse unlink(final String targetId, final String targetIdType) {
        return new KakaoUnlinkResponse(parseTargetId(targetId));
    }

    /**
     * 카카오 사용자 정보 응답을 모사한다.
     * <ul>
     *   <li>token == "invalid" → {@link InvalidKakaoAccessTokenException}</li>
     *   <li>token == "no-email" → kakao_account.email 누락 응답</li>
     *   <li>그 외 → 토큰을 식별자에 포함한 정상 응답 (테스트에서 사용자별 분기를 위해)</li>
     * </ul>
     */
    @Override
    public Map<String, Object> getUserInfo(final String userAccessToken) {
        if (INVALID_TOKEN_MARKER.equals(userAccessToken)) {
            throw new InvalidKakaoAccessTokenException("유효하지 않은 카카오 토큰입니다.");
        }

        long syntheticId = Math.abs((long) userAccessToken.hashCode());
        String email = NO_EMAIL_TOKEN_MARKER.equals(userAccessToken)
                ? null
                : "stub-" + syntheticId + "@example.com";

        Map<String, Object> properties = new HashMap<>();
        properties.put("nickname", "스텁사용자-" + syntheticId);
        properties.put("profile_image", "https://example.com/profile-" + syntheticId + ".jpg");
        properties.put("thumbnail_image", "https://example.com/thumb-" + syntheticId + ".jpg");

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", email);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", syntheticId);
        attributes.put("properties", properties);
        attributes.put("kakao_account", kakaoAccount);
        return attributes;
    }

    private Long parseTargetId(final String targetId) {
        try {
            return Long.parseLong(targetId);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}

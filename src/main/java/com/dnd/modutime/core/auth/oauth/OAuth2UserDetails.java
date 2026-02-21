package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.exception.OAuth2UserParsingException;
import com.dnd.modutime.core.auth.oauth.kakao.KakaoAttributes;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public record OAuth2UserDetails(

        String name,
        String email,
        String profileImage,
        String thumbnailImage,
        OAuth2Provider oAuth2Provider
) {
    public static OAuth2UserDetails of(final String registrationId, final Map<String, Object> attributes, final ObjectMapper objectMapper) {
        return switch (registrationId) {
            case "kakao" -> ofKakao(attributes, objectMapper);
            default -> throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        };
    }

    /**
     * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info-response"> 카카오 사용자 정보 응답</a>
     * @param attributes 카카오로 로그인한 사용자 정보
     */
    private static OAuth2UserDetails ofKakao(final Map<String, Object> attributes, final ObjectMapper objectMapper) {
        KakaoAttributes kakaoAttributes;
        try {
            kakaoAttributes = objectMapper.convertValue(attributes, KakaoAttributes.class);
        } catch (IllegalArgumentException e) {
            log.error("사용자 정보 파싱 실패", e);
            throw new OAuth2UserParsingException("사용자 정보를 파싱하는 데 실패했습니다. 문제를 확인하고 조치해야 합니다.", e.getCause(), ErrorCode.MT500);
        }

        Map<String, Object> properties = kakaoAttributes.properties();
        String name = (String) properties.getOrDefault("nickname", null);
        String profileImage = (String) properties.getOrDefault("profile_image", null);
        String thumbnailImage = (String) properties.getOrDefault("thumbnail_image", null);

        Map<String, Object> kakaoAccount = kakaoAttributes.kakao_account();
        String email = (String) kakaoAccount.getOrDefault("email", null);

        return new OAuth2UserDetails(name, email, profileImage, thumbnailImage, OAuth2Provider.KAKAO);
    }

    public User toEntity() {
        return new User(this.name, this.email, this.profileImage, this.thumbnailImage, this.oAuth2Provider);
    }
}

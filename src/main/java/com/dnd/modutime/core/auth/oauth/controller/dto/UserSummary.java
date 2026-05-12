package com.dnd.modutime.core.auth.oauth.controller.dto;

import com.dnd.modutime.core.user.User;

/**
 * 로그인 응답에 포함되는 사용자 요약 정보.
 */
public record UserSummary(
        String name,
        String email,
        String profileImage,
        String thumbnailImage
) {
    public static UserSummary from(final User user) {
        return new UserSummary(
                user.getName(),
                user.getEmail(),
                user.getProfileImage(),
                user.getThumbnailImage()
        );
    }
}

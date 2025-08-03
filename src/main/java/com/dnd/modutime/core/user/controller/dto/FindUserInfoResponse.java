package com.dnd.modutime.core.user.controller.dto;

import com.dnd.modutime.core.user.User;

public record FindUserInfoResponse(
        String name,
        String email,
        String profileImage,
        String thumbnailImage
) {
    public FindUserInfoResponse(User user) {
        this(user.getName(), user.getEmail(), user.getProfileImage(), user.getThumbnailImage());
    }
}

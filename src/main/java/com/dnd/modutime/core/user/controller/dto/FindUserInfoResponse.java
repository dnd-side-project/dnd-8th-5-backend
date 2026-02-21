package com.dnd.modutime.core.user.controller.dto;

import com.dnd.modutime.core.user.User;

public record FindUserInfoResponse(
        String name
) {
    public FindUserInfoResponse(User user) {
        this(user.getName());
    }
}

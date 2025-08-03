package com.dnd.modutime.core.user.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.user.controller.dto.FindUserInfoResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserQueryController {

    @GetMapping("/api/user/me")
    public FindUserInfoResponse findUserInfo(@AuthenticationPrincipal OAuth2User user) {
        return new FindUserInfoResponse(user.user());
    }
}

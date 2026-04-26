package com.dnd.modutime.core.user.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.user.application.UserWithdrawService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserCommandController {

    private final UserWithdrawService userWithdrawService;

    public UserCommandController(final UserWithdrawService userWithdrawService) {
        this.userWithdrawService = userWithdrawService;
    }

    @DeleteMapping("/api/v1/users/me")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal OAuth2User user) {
        userWithdrawService.withdraw(user.user().getId());
        return ResponseEntity.noContent().build();
    }
}

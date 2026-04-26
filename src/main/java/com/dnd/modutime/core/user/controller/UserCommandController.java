package com.dnd.modutime.core.user.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.user.application.UserWithdrawFacade;
import com.dnd.modutime.core.user.controller.dto.UserWithdrawRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserCommandController {

    private final UserWithdrawFacade userWithdrawFacade;

    public UserCommandController(final UserWithdrawFacade userWithdrawFacade) {
        this.userWithdrawFacade = userWithdrawFacade;
    }

    @DeleteMapping("/api/v1/users/me")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal OAuth2User user,
                                         @Valid @RequestBody UserWithdrawRequest request) {
        userWithdrawFacade.withdraw(user.user().getId(), request.reason());
        return ResponseEntity.noContent().build();
    }
}

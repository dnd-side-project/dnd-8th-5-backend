package com.dnd.modutime.core.notification.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.notification.application.DeviceTokenService;
import com.dnd.modutime.core.notification.application.command.DeviceTokenRegisterCommand;
import com.dnd.modutime.core.notification.controller.dto.DeviceTokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    public DeviceTokenController(DeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping("/api/v1/device-tokens")
    public ResponseEntity<Void> register(
            @RequestBody @Valid DeviceTokenRequest request,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        var command = DeviceTokenRegisterCommand.of(request.token(), oAuth2User.user().getId());
        var isNew = deviceTokenService.register(command);
        var status = isNew ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).build();
    }

    @DeleteMapping("/api/v1/device-tokens")
    public ResponseEntity<Void> unregister(
            @RequestParam String token,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        deviceTokenService.unregister(token, oAuth2User.user().getId());
        return ResponseEntity.noContent().build();
    }
}

package com.dnd.modutime.controller;

import com.dnd.modutime.auth.AuthService;
import com.dnd.modutime.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/{roomUuid}/login")
    public ResponseEntity<Void> login(@PathVariable String roomUuid,
                                      @RequestBody LoginRequest loginRequest) {
        authService.login(roomUuid, loginRequest);
        return ResponseEntity.ok().build();
    }
}
